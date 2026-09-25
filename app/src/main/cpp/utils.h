#ifndef BYEDPI_UTILS_H
#define BYEDPI_UTILS_H

#include <getopt.h>
#include <jni.h>
#include "byedpi/params.h"

extern struct params default_params;

void reset_params(void);
int parse_args(int argc, char **argv);
int init(void);
void clear_params(char *line, char **argv);
void dump_all_cache(void);

void add_arg(char ***argv, int *argc, int *capacity, const char *arg);
int create_socket_from_cmdline(JNIEnv *env, jobjectArray args);
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
        jint fake_offset,
        jboolean no_ipv6,
        jstring conn_ip,
        jboolean wait_send,
        jint await_int,
        jboolean md5sig,
        jstring fake_data,
        jstring fake_tls_mod,
        jint tlsminor,
        jstring round,
        jstring pf,
        jstring ipset,
        jstring auto_val,
        jstring auto_mode,
        jstring timeout);

int stop_proxy_loop(int fd);

#endif
