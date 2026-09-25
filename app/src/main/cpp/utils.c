#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <getopt.h>
#include <netdb.h>
#include <fcntl.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netinet/in.h>

#include <jni.h>
#include <android/log.h>

#include "byedpi/error.h"
#include "byedpi/proxy.h"
#include "byedpi/params.h"
#include "byedpi/packets.h"
#include "utils.h"

struct params default_params;

void reset_params(void) {
    clear_params(NULL, NULL);
    params = default_params;
}

void add_arg(char ***argv, int *argc, int *capacity, const char *arg) {
    if (!arg) {
        return;
    }
    if (*argc + 1 >= *capacity) {
        *capacity *= 2;
        *argv = realloc(*argv, sizeof(char *) * (*capacity));
    }
    (*argv)[(*argc)++] = strdup(arg);
    (*argv)[*argc] = NULL;
}

int create_socket_from_cmdline(JNIEnv *env, jobjectArray args) {
    int argc = (*env)->GetArrayLength(env, args);
    char **argv = malloc(sizeof(char *) * (argc + 1));
    if (!argv) {
        return -1;
    }
    for (int i = 0; i < argc; i++) {
        jstring arg = (jstring) (*env)->GetObjectArrayElement(env, args, i);
        const char *arg_str = (*env)->GetStringUTFChars(env, arg, 0);
        argv[i] = strdup(arg_str);
        (*env)->ReleaseStringUTFChars(env, arg, arg_str);
    }
    argv[argc] = NULL;

    optind = 1;
    optreset = 1;

    int res = parse_args(argc, argv);
    for (int i = 0; i < argc; i++) {
        free(argv[i]);
    }
    free(argv);

    if (res < 0) {
        uniperror("parse_args");
        reset_params();
        return -1;
    }

    if (init() < 0) {
        uniperror("init");
        reset_params();
        return -1;
    }

    int fd = listen_socket(&params.laddr);
    if (fd < 0) {
        uniperror("listen_socket");
        reset_params();
        return -1;
    }
    LOG(LOG_S, "listen_socket, fd: %d", fd);

    return fd;
}

int create_socket_from_ui(
        JNIEnv *env,
        jstring ip,
        jint port,
        jint max_connections,
        jint buffer_size,
        jint default_ttl,
        jboolean custom_ttl,
        jboolean no_domain,
        jboolean desync_http,
        jboolean desync_https,
        jboolean desync_udp,
        jint desync_method,
        jint split_position,
        jboolean split_at_host,
        jint fake_ttl,
        jstring fake_sni,
        jbyte custom_oob_char,
        jboolean host_mixed_case,
        jboolean domain_mixed_case,
        jboolean host_remove_spaces,
        jboolean tls_record_split,
        jint tls_record_split_position,
        jboolean tls_record_split_at_sni,
        jint hosts_mode,
        jstring hosts,
        jboolean tfo,
        jint udp_fake_count,
        jboolean drop_sack,
        jint fake_offset) {
    int capacity = 64;
    int argc = 0;
    char **argv = malloc(sizeof(char *) * capacity);
    if (!argv) {
        return -1;
    }
    argv[0] = NULL;

    add_arg(&argv, &argc, &capacity, "ciadpi");

    // IP
    const char *ip_str = (*env)->GetStringUTFChars(env, ip, 0);
    if (ip_str != NULL && strlen(ip_str) > 0) {
        add_arg(&argv, &argc, &capacity, "-i");
        add_arg(&argv, &argc, &capacity, ip_str);
    }
    if (ip_str != NULL) {
        (*env)->ReleaseStringUTFChars(env, ip, ip_str);
    }

    // Port
    char port_buf[16];
    snprintf(port_buf, sizeof(port_buf), "%d", port);
    add_arg(&argv, &argc, &capacity, "-p");
    add_arg(&argv, &argc, &capacity, port_buf);

    // Max connections
    if (max_connections > 0) {
        char mc_buf[16];
        snprintf(mc_buf, sizeof(mc_buf), "%d", max_connections);
        add_arg(&argv, &argc, &capacity, "-c");
        add_arg(&argv, &argc, &capacity, mc_buf);
    }

    // Buffer size
    if (buffer_size > 0) {
        char bs_buf[16];
        snprintf(bs_buf, sizeof(bs_buf), "%d", buffer_size);
        add_arg(&argv, &argc, &capacity, "-b");
        add_arg(&argv, &argc, &capacity, bs_buf);
    }

    // Default TTL
    if (custom_ttl && default_ttl > 0) {
        char ttl_buf[16];
        snprintf(ttl_buf, sizeof(ttl_buf), "%d", default_ttl);
        add_arg(&argv, &argc, &capacity, "-g");
        add_arg(&argv, &argc, &capacity, ttl_buf);
    }

    // No domain
    if (no_domain) {
        add_arg(&argv, &argc, &capacity, "-N");
    }

    // TFO
    if (tfo) {
        add_arg(&argv, &argc, &capacity, "-F");
    }

    // Hosts filter
    const char *hosts_str = NULL;
    if (hosts != NULL) {
        hosts_str = (*env)->GetStringUTFChars(env, hosts, 0);
    }

    if (hosts_mode == 1 && hosts_str != NULL && strlen(hosts_str) > 0) {
        size_t hlen = strlen(hosts_str) + 2;
        char *harg = malloc(hlen);
        if (harg != NULL) {
            snprintf(harg, hlen, ":%s", hosts_str);
            add_arg(&argv, &argc, &capacity, "-H");
            add_arg(&argv, &argc, &capacity, harg);
            free(harg);
        }
        add_arg(&argv, &argc, &capacity, "-A");
        add_arg(&argv, &argc, &capacity, "none");
    } else if (hosts_mode == 2 && hosts_str != NULL && strlen(hosts_str) > 0) {
        size_t hlen = strlen(hosts_str) + 2;
        char *harg = malloc(hlen);
        if (harg != NULL) {
            snprintf(harg, hlen, ":%s", hosts_str);
            add_arg(&argv, &argc, &capacity, "-H");
            add_arg(&argv, &argc, &capacity, harg);
            free(harg);
        }
    }

    // Protocol whitelist
    char proto_buf[16] = {0};
    if (desync_https || desync_http || desync_udp) {
        if (!(desync_https && desync_http && desync_udp)) {
            int pidx = 0;
            if (desync_https) {
                proto_buf[pidx++] = 't';
            }
            if (desync_http) {
                if (pidx > 0) {
                    proto_buf[pidx++] = ',';
                }
                proto_buf[pidx++] = 'h';
            }
            if (desync_udp) {
                if (pidx > 0) {
                    proto_buf[pidx++] = ',';
                }
                proto_buf[pidx++] = 'u';
            }
            proto_buf[pidx] = '\0';
            add_arg(&argv, &argc, &capacity, "-K");
            add_arg(&argv, &argc, &capacity, proto_buf);
        }
    }

    // Desync method and position
    if (desync_method > 0 && desync_method <= 5) {
        const char *flag_suffix = "";
        if (split_at_host) {
            if (desync_https || !desync_http) {
                flag_suffix = "+s";
            } else {
                flag_suffix = "+h";
            }
        }
        char pos_buf[32];
        snprintf(pos_buf, sizeof(pos_buf), "%d%s", split_position, flag_suffix);

        const char *m_flag = NULL;
        switch (desync_method) {
            case 1:
                m_flag = "-s";
                break;
            case 2:
                m_flag = "-d";
                break;
            case 3:
                m_flag = "-f";
                break;
            case 4:
                m_flag = "-o";
                break;
            case 5:
                m_flag = "-q";
                break;
            default:
                break;
        }
        if (m_flag != NULL) {
            add_arg(&argv, &argc, &capacity, m_flag);
            add_arg(&argv, &argc, &capacity, pos_buf);
        }
    }

    // Fake options
    if (desync_method == 3) {
        if (fake_ttl > 0) {
            char fttl_buf[16];
            snprintf(fttl_buf, sizeof(fttl_buf), "%d", fake_ttl);
            add_arg(&argv, &argc, &capacity, "-t");
            add_arg(&argv, &argc, &capacity, fttl_buf);
        }
        if (fake_offset > 0) {
            char foff_buf[16];
            snprintf(foff_buf, sizeof(foff_buf), "%d", fake_offset);
            add_arg(&argv, &argc, &capacity, "-O");
            add_arg(&argv, &argc, &capacity, foff_buf);
        }
        const char *sni_str = (fake_sni != NULL) ? (*env)->GetStringUTFChars(env, fake_sni, 0) : NULL;
        if (sni_str != NULL && strlen(sni_str) > 0) {
            add_arg(&argv, &argc, &capacity, "-n");
            add_arg(&argv, &argc, &capacity, sni_str);
        }
        if (sni_str != NULL) {
            (*env)->ReleaseStringUTFChars(env, fake_sni, sni_str);
        }
    }

    // Custom OOB char
    if ((desync_method == 4 || desync_method == 5) && custom_oob_char != 0) {
        char oob_buf[16];
        snprintf(oob_buf, sizeof(oob_buf), "\\x%02x", (unsigned char)custom_oob_char);
        add_arg(&argv, &argc, &capacity, "-e");
        add_arg(&argv, &argc, &capacity, oob_buf);
    }

    // HTTP modifications
    char mod_buf[16] = {0};
    int midx = 0;
    if (host_mixed_case) {
        mod_buf[midx++] = 'h';
    }
    if (domain_mixed_case) {
        if (midx > 0) {
            mod_buf[midx++] = ',';
        }
        mod_buf[midx++] = 'd';
    }
    if (host_remove_spaces) {
        if (midx > 0) {
            mod_buf[midx++] = ',';
        }
        mod_buf[midx++] = 'r';
    }
    if (midx > 0) {
        mod_buf[midx] = '\0';
        add_arg(&argv, &argc, &capacity, "-M");
        add_arg(&argv, &argc, &capacity, mod_buf);
    }

    // TLS record split
    if (tls_record_split) {
        const char *tls_flag = tls_record_split_at_sni ? "+s" : "";
        char tls_buf[32];
        snprintf(tls_buf, sizeof(tls_buf), "%d%s", tls_record_split_position, tls_flag);
        add_arg(&argv, &argc, &capacity, "-r");
        add_arg(&argv, &argc, &capacity, tls_buf);
    }

    // UDP fake count
    if (udp_fake_count > 0) {
        char ufake_buf[16];
        snprintf(ufake_buf, sizeof(ufake_buf), "%d", udp_fake_count);
        add_arg(&argv, &argc, &capacity, "-a");
        add_arg(&argv, &argc, &capacity, ufake_buf);
    }

    // Drop SACK
    if (drop_sack) {
        add_arg(&argv, &argc, &capacity, "-Y");
    }

    // If whitelist or proto whitelist was specified, pass -A none at the end
    if ((hosts_mode == 2 && hosts_str != NULL && strlen(hosts_str) > 0) || strlen(proto_buf) > 0) {
        add_arg(&argv, &argc, &capacity, "-A");
        add_arg(&argv, &argc, &capacity, "none");
    }

    if (hosts_str != NULL) {
        (*env)->ReleaseStringUTFChars(env, hosts, hosts_str);
    }

    optind = 1;
    optreset = 1;

    int res = parse_args(argc, argv);
    for (int i = 0; i < argc; i++) {
        free(argv[i]);
    }
    free(argv);

    if (res < 0) {
        uniperror("parse_args");
        reset_params();
        return -1;
    }

    if (init() < 0) {
        uniperror("init");
        reset_params();
        return -1;
    }

    int fd = listen_socket(&params.laddr);
    if (fd < 0) {
        uniperror("listen_socket");
        reset_params();
        return -1;
    }
    LOG(LOG_S, "listen_socket, fd: %d", fd);

    return fd;
}

int stop_proxy_loop(int fd) {
    if (fd < 0) {
        return 0;
    }

    // 1. Shutdown listening socket to prevent new accepts and signal failure
    shutdown(fd, SHUT_RDWR);

    // 2. Connect to local port to wake up epoll_wait on listening socket
    union sockaddr_u addr = params.laddr;
    int family = addr.sa.sa_family;
    if (family != AF_INET && family != AF_INET6) {
        family = AF_INET;
    }

    int sock = socket(family, SOCK_STREAM, 0);
    if (sock >= 0) {
        int flags = fcntl(sock, F_GETFL, 0);
        if (flags >= 0) {
            fcntl(sock, F_SETFL, flags | O_NONBLOCK);
        }
        if (family == AF_INET) {
            if (addr.in.sin_addr.s_addr == INADDR_ANY) {
                addr.in.sin_addr.s_addr = inet_addr("127.0.0.1");
            }
            connect(sock, &addr.sa, sizeof(struct sockaddr_in));
        } else {
            connect(sock, &addr.sa, sizeof(struct sockaddr_in6));
        }
        close(sock);
    }

    return 0;
}
