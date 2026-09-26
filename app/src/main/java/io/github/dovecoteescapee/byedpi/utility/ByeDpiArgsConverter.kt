package io.github.dovecoteescapee.byedpi.utility

import android.content.SharedPreferences
import androidx.core.content.edit

object ByeDpiArgsConverter {
    fun uiPreferencesToCmdArgs(prefs: SharedPreferences): String {
        val args = mutableListOf<String>()

        val ip = prefs.getString("byedpi_proxy_ip", "127.0.0.1") ?: "127.0.0.1"
        if (ip.isNotBlank() && ip != "127.0.0.1") {
            args.add("-i")
            args.add(ip)
        }

        val port = prefs.getString("byedpi_proxy_port", "1080") ?: "1080"
        if (port.isNotBlank() && port != "1080") {
            args.add("-p")
            args.add(port)
        }

        val maxConn = prefs.getString("byedpi_max_connections", "512") ?: "512"
        if (maxConn.isNotBlank() && maxConn != "512" && (maxConn.toIntOrNull() ?: 0) > 0) {
            args.add("-c")
            args.add(maxConn)
        }

        val bufferSize = prefs.getString("byedpi_buffer_size", "16384") ?: "16384"
        if (bufferSize.isNotBlank() && bufferSize != "16384" && (bufferSize.toIntOrNull() ?: 0) > 0) {
            args.add("-b")
            args.add(bufferSize)
        }

        val defaultTtl = prefs.getString("byedpi_default_ttl", "0") ?: "0"
        if (defaultTtl.isNotBlank() && (defaultTtl.toIntOrNull() ?: 0) > 0) {
            args.add("-g")
            args.add(defaultTtl)
        }

        if (prefs.getBoolean("byedpi_no_domain", false)) {
            args.add("-N")
        }

        if (prefs.getBoolean("byedpi_no_ipv6", false)) {
            args.add("-X")
        }

        val connIp = prefs.getString("byedpi_conn_ip", "") ?: ""
        if (connIp.isNotBlank()) {
            args.add("-I")
            args.add(connIp)
        }

        if (prefs.getBoolean("byedpi_wait_send", false)) {
            args.add("-Z")
        }

        val awaitInt = prefs.getString("byedpi_await_int", "") ?: ""
        if (awaitInt.isNotBlank() && (awaitInt.toIntOrNull() ?: 0) > 0) {
            args.add("-W")
            args.add(awaitInt)
        }

        if (prefs.getBoolean("byedpi_tcp_fast_open", false)) {
            args.add("-F")
        }

        val hostsMode = prefs.getString("byedpi_hosts_mode", "disable") ?: "disable"
        val hosts =
            when (hostsMode) {
                "blacklist" -> prefs.getString("byedpi_hosts_blacklist", "") ?: ""
                "whitelist" -> prefs.getString("byedpi_hosts_whitelist", "") ?: ""
                else -> ""
            }
        if (hostsMode == "blacklist" && hosts.isNotBlank()) {
            args.add("-H")
            args.add(":$hosts")
            args.add("-A")
            args.add("none")
        } else if (hostsMode == "whitelist" && hosts.isNotBlank()) {
            args.add("-H")
            args.add(":$hosts")
        }

        val desyncHttp = prefs.getBoolean("byedpi_desync_http", true)
        val desyncHttps = prefs.getBoolean("byedpi_desync_https", true)
        val desyncUdp = prefs.getBoolean("byedpi_desync_udp", false)

        val protoList = mutableListOf<Char>()
        if (desyncHttps) {
            protoList.add('t')
        }
        if (desyncHttp) {
            protoList.add('h')
        }
        if (desyncUdp) {
            protoList.add('u')
        }
        val protoStr = protoList.joinToString(",")
        if (protoStr.isNotEmpty() && protoStr != "t,h,u") {
            args.add("-K")
            args.add(protoStr)
        }

        val desyncMethod = prefs.getString("byedpi_desync_method", "none") ?: "none"
        val splitPosition = prefs.getString("byedpi_split_position", "0") ?: "0"
        val splitAtHost = prefs.getBoolean("byedpi_split_at_host", false)

        if (desyncMethod != "none") {
            val suffix =
                if (splitAtHost) {
                    if (desyncHttps || !desyncHttp) {
                        "+s"
                    } else {
                        "+h"
                    }
                } else {
                    ""
                }
            val posArg = "$splitPosition$suffix"

            when (desyncMethod) {
                "split" -> {
                    args.add("-s")
                    args.add(posArg)
                }
                "disorder" -> {
                    args.add("-d")
                    args.add(posArg)
                }
                "fake" -> {
                    args.add("-f")
                    args.add(posArg)
                }
                "oob" -> {
                    args.add("-o")
                    args.add(posArg)
                }
                "disoob" -> {
                    args.add("-q")
                    args.add(posArg)
                }
            }
        }

        if (desyncMethod == "fake") {
            val fakeTtl = prefs.getString("byedpi_fake_ttl", "0") ?: "0"
            if (fakeTtl.isNotBlank() && (fakeTtl.toIntOrNull() ?: 0) > 0) {
                args.add("-t")
                args.add(fakeTtl)
            }

            val fakeOffset = prefs.getString("byedpi_fake_offset", "0") ?: "0"
            if (fakeOffset.isNotBlank() && (fakeOffset.toIntOrNull() ?: 0) > 0) {
                args.add("-O")
                args.add(fakeOffset)
            }

            if (prefs.getBoolean("byedpi_md5sig", false)) {
                args.add("-S")
            }

            val fakeData = prefs.getString("byedpi_fake_data", "") ?: ""
            if (fakeData.isNotBlank()) {
                args.add("-l")
                args.add(fakeData)
            }

            val fakeTlsMod = prefs.getString("byedpi_fake_tls_mod", "") ?: ""
            if (fakeTlsMod.isNotBlank()) {
                args.add("-Q")
                args.add(fakeTlsMod)
            }

            val tlsminor = prefs.getString("byedpi_tlsminor", "") ?: ""
            if (tlsminor.isNotBlank() && (tlsminor.toIntOrNull() ?: -1) >= 0) {
                args.add("-m")
                args.add(tlsminor)
            }

            val fakeSni = prefs.getString("byedpi_fake_sni", "") ?: ""
            if (fakeSni.isNotBlank()) {
                args.add("-n")
                args.add(fakeSni)
            }
        }

        if (desyncMethod == "oob" || desyncMethod == "disoob") {
            val oobData = prefs.getString("byedpi_oob_data", "a") ?: "a"
            if (oobData.isNotEmpty()) {
                val byteVal = oobData[0].code
                args.add("-e")
                args.add(String.format("\\x%02x", byteVal))
            }
        }

        val httpMods = mutableListOf<Char>()
        if (prefs.getBoolean("byedpi_host_mixed_case", false)) {
            httpMods.add('h')
        }
        if (prefs.getBoolean("byedpi_domain_mixed_case", false)) {
            httpMods.add('d')
        }
        if (prefs.getBoolean("byedpi_host_remove_spaces", false)) {
            httpMods.add('r')
        }
        if (httpMods.isNotEmpty()) {
            args.add("-M")
            args.add(httpMods.joinToString(","))
        }

        if (prefs.getBoolean("byedpi_tlsrec_enabled", false)) {
            val tlsPos = prefs.getString("byedpi_tlsrec_position", "0") ?: "0"
            val atSni = prefs.getBoolean("byedpi_tlsrec_at_sni", false)
            val tlsArg =
                if (atSni) {
                    "$tlsPos+s"
                } else {
                    tlsPos
                }
            args.add("-r")
            args.add(tlsArg)
        }

        val udpFakeCount = prefs.getString("byedpi_udp_fake_count", "0") ?: "0"
        if (udpFakeCount.isNotBlank() && (udpFakeCount.toIntOrNull() ?: 0) > 0) {
            args.add("-a")
            args.add(udpFakeCount)
        }

        if (prefs.getBoolean("byedpi_drop_sack", false)) {
            args.add("-Y")
        }

        val round = prefs.getString("byedpi_round", "") ?: ""
        if (round.isNotBlank()) {
            args.add("-R")
            args.add(round)
        }

        val pf = prefs.getString("byedpi_pf", "") ?: ""
        if (pf.isNotBlank()) {
            args.add("-V")
            args.add(pf)
        }

        val ipset = prefs.getString("byedpi_ipset", "") ?: ""
        if (ipset.isNotBlank()) {
            args.add("-j")
            args.add(":$ipset")
        }

        val auto = prefs.getString("byedpi_auto", "") ?: ""
        if (auto.isNotBlank()) {
            args.add("-A")
            args.add(auto)
        }

        val autoMode = prefs.getString("byedpi_auto_mode", "none") ?: "none"
        if (autoMode.isNotBlank() && autoMode != "none") {
            args.add("-L")
            args.add(autoMode)
        }

        val timeout = prefs.getString("byedpi_timeout", "") ?: ""
        if (timeout.isNotBlank()) {
            args.add("-T")
            args.add(timeout)
        }

        return args.joinToString(" ")
    }

    fun applyCmdArgsToUiPreferences(
        argsStr: String,
        prefs: SharedPreferences,
    ) {
        val trimmed = argsStr.trim()
        val firstArgIndex = trimmed.indexOf("-")
        val effectiveArgsStr =
            if (firstArgIndex >= 0) {
                trimmed.substring(firstArgIndex)
            } else {
                trimmed
            }

        val tokens = shellSplit(effectiveArgsStr)
        if (tokens.isEmpty()) {
            return
        }

        var i = 0
        val updates = mutableMapOf<String, Any>()

        while (i < tokens.size) {
            val token = tokens[i]
            when {
                token == "-i" && i + 1 < tokens.size -> {
                    updates["byedpi_proxy_ip"] = tokens[++i]
                }
                token.startsWith("-i") && token.length > 2 -> {
                    updates["byedpi_proxy_ip"] = token.substring(2)
                }

                token == "-p" && i + 1 < tokens.size -> {
                    updates["byedpi_proxy_port"] = tokens[++i]
                }
                token.startsWith("-p") && token.length > 2 -> {
                    updates["byedpi_proxy_port"] = token.substring(2)
                }

                token == "-c" && i + 1 < tokens.size -> {
                    updates["byedpi_max_connections"] = tokens[++i]
                }
                token.startsWith("-c") && token.length > 2 -> {
                    updates["byedpi_max_connections"] = token.substring(2)
                }

                token == "-b" && i + 1 < tokens.size -> {
                    updates["byedpi_buffer_size"] = tokens[++i]
                }
                token.startsWith("-b") && token.length > 2 -> {
                    updates["byedpi_buffer_size"] = token.substring(2)
                }

                token == "-g" && i + 1 < tokens.size -> {
                    updates["byedpi_default_ttl"] = tokens[++i]
                }
                token.startsWith("-g") && token.length > 2 -> {
                    updates["byedpi_default_ttl"] = token.substring(2)
                }

                token == "-N" -> {
                    updates["byedpi_no_domain"] = true
                }

                token == "-X" -> {
                    updates["byedpi_no_ipv6"] = true
                }

                token == "-I" && i + 1 < tokens.size -> {
                    updates["byedpi_conn_ip"] = tokens[++i]
                }
                token.startsWith("-I") && token.length > 2 -> {
                    updates["byedpi_conn_ip"] = token.substring(2)
                }

                token == "-Z" -> {
                    updates["byedpi_wait_send"] = true
                }

                token == "-W" && i + 1 < tokens.size -> {
                    updates["byedpi_await_int"] = tokens[++i]
                }
                token.startsWith("-W") && token.length > 2 -> {
                    updates["byedpi_await_int"] = token.substring(2)
                }

                token == "-F" -> {
                    updates["byedpi_tcp_fast_open"] = true
                }

                token == "-H" && i + 1 < tokens.size -> {
                    val raw = tokens[++i]
                    val hostVal =
                        if (raw.startsWith(":")) {
                            raw.substring(1)
                        } else {
                            raw
                        }
                    updates["byedpi_hosts_mode"] = "blacklist"
                    updates["byedpi_hosts_blacklist"] = hostVal
                }
                token.startsWith("-H") && token.length > 2 -> {
                    val raw = token.substring(2)
                    val hostVal =
                        if (raw.startsWith(":")) {
                            raw.substring(1)
                        } else {
                            raw
                        }
                    updates["byedpi_hosts_mode"] = "blacklist"
                    updates["byedpi_hosts_blacklist"] = hostVal
                }

                token == "-K" && i + 1 < tokens.size -> {
                    val p = tokens[++i]
                    updates["byedpi_desync_https"] = p.contains('t')
                    updates["byedpi_desync_http"] = p.contains('h')
                    updates["byedpi_desync_udp"] = p.contains('u')
                }
                token.startsWith("-K") && token.length > 2 -> {
                    val p = token.substring(2)
                    updates["byedpi_desync_https"] = p.contains('t')
                    updates["byedpi_desync_http"] = p.contains('h')
                    updates["byedpi_desync_udp"] = p.contains('u')
                }

                token in listOf("-s", "-d", "-f", "-o", "-q") -> {
                    val method =
                        when (token) {
                            "-s" -> "split"
                            "-d" -> "disorder"
                            "-f" -> "fake"
                            "-o" -> "oob"
                            "-q" -> "disoob"
                            else -> "none"
                        }
                    updates["byedpi_desync_method"] = method
                    if (i + 1 < tokens.size && !tokens[i + 1].startsWith("-")) {
                        val posStr = tokens[++i]
                        val isAtHost = posStr.endsWith("+s") || posStr.endsWith("+h")
                        val cleanPos = posStr.replace("+s", "").replace("+h", "")
                        updates["byedpi_split_position"] = cleanPos
                        updates["byedpi_split_at_host"] = isAtHost
                    }
                }
                token.length > 2 &&
                    (
                        token.startsWith("-s") ||
                            token.startsWith("-d") ||
                            token.startsWith("-f") ||
                            token.startsWith("-o") ||
                            token.startsWith("-q")
                    ) -> {
                    val flag = token.substring(0, 2)
                    val posStr = token.substring(2)
                    val method =
                        when (flag) {
                            "-s" -> "split"
                            "-d" -> "disorder"
                            "-f" -> "fake"
                            "-o" -> "oob"
                            "-q" -> "disoob"
                            else -> "none"
                        }
                    updates["byedpi_desync_method"] = method
                    val isAtHost = posStr.endsWith("+s") || posStr.endsWith("+h")
                    val cleanPos = posStr.replace("+s", "").replace("+h", "")
                    updates["byedpi_split_position"] = cleanPos
                    updates["byedpi_split_at_host"] = isAtHost
                }

                token == "-t" && i + 1 < tokens.size -> {
                    updates["byedpi_fake_ttl"] = tokens[++i]
                }
                token.startsWith("-t") && token.length > 2 -> {
                    updates["byedpi_fake_ttl"] = token.substring(2)
                }

                token == "-O" && i + 1 < tokens.size -> {
                    updates["byedpi_fake_offset"] = tokens[++i]
                }
                token.startsWith("-O") && token.length > 2 -> {
                    updates["byedpi_fake_offset"] = token.substring(2)
                }

                token == "-S" -> {
                    updates["byedpi_md5sig"] = true
                }

                token == "-l" && i + 1 < tokens.size -> {
                    updates["byedpi_fake_data"] = tokens[++i]
                }
                token.startsWith("-l") && token.length > 2 -> {
                    updates["byedpi_fake_data"] = token.substring(2)
                }

                token == "-Q" && i + 1 < tokens.size -> {
                    updates["byedpi_fake_tls_mod"] = tokens[++i]
                }
                token.startsWith("-Q") && token.length > 2 -> {
                    updates["byedpi_fake_tls_mod"] = token.substring(2)
                }

                token == "-m" && i + 1 < tokens.size -> {
                    updates["byedpi_tlsminor"] = tokens[++i]
                }
                token.startsWith("-m") && token.length > 2 -> {
                    updates["byedpi_tlsminor"] = token.substring(2)
                }

                token == "-n" && i + 1 < tokens.size -> {
                    updates["byedpi_fake_sni"] = tokens[++i]
                }
                token.startsWith("-n") && token.length > 2 -> {
                    updates["byedpi_fake_sni"] = token.substring(2)
                }

                token == "-e" && i + 1 < tokens.size -> {
                    val raw = tokens[++i]
                    updates["byedpi_oob_data"] = parseOobChar(raw)
                }
                token.startsWith("-e") && token.length > 2 -> {
                    val raw = token.substring(2)
                    updates["byedpi_oob_data"] = parseOobChar(raw)
                }

                token == "-M" && i + 1 < tokens.size -> {
                    val m = tokens[++i]
                    updates["byedpi_host_mixed_case"] = m.contains('h')
                    updates["byedpi_domain_mixed_case"] = m.contains('d')
                    updates["byedpi_host_remove_spaces"] = m.contains('r')
                }
                token.startsWith("-M") && token.length > 2 -> {
                    val m = token.substring(2)
                    updates["byedpi_host_mixed_case"] = m.contains('h')
                    updates["byedpi_domain_mixed_case"] = m.contains('d')
                    updates["byedpi_host_remove_spaces"] = m.contains('r')
                }

                token == "-r" && i + 1 < tokens.size -> {
                    updates["byedpi_tlsrec_enabled"] = true
                    val r = tokens[++i]
                    val atSni = r.endsWith("+s")
                    val pos = r.replace("+s", "")
                    updates["byedpi_tlsrec_position"] = pos
                    updates["byedpi_tlsrec_at_sni"] = atSni
                }
                token.startsWith("-r") && token.length > 2 -> {
                    updates["byedpi_tlsrec_enabled"] = true
                    val r = token.substring(2)
                    val atSni = r.endsWith("+s")
                    val pos = r.replace("+s", "")
                    updates["byedpi_tlsrec_position"] = pos
                    updates["byedpi_tlsrec_at_sni"] = atSni
                }

                token == "-a" && i + 1 < tokens.size -> {
                    updates["byedpi_udp_fake_count"] = tokens[++i]
                }
                token.startsWith("-a") && token.length > 2 -> {
                    updates["byedpi_udp_fake_count"] = token.substring(2)
                }

                token == "-Y" -> {
                    updates["byedpi_drop_sack"] = true
                }

                token == "-R" && i + 1 < tokens.size -> {
                    updates["byedpi_round"] = tokens[++i]
                }
                token.startsWith("-R") && token.length > 2 -> {
                    updates["byedpi_round"] = token.substring(2)
                }

                token == "-V" && i + 1 < tokens.size -> {
                    updates["byedpi_pf"] = tokens[++i]
                }
                token.startsWith("-V") && token.length > 2 -> {
                    updates["byedpi_pf"] = token.substring(2)
                }

                token == "-j" && i + 1 < tokens.size -> {
                    val raw = tokens[++i]
                    updates["byedpi_ipset"] =
                        if (raw.startsWith(":")) {
                            raw.substring(1)
                        } else {
                            raw
                        }
                }
                token.startsWith("-j") && token.length > 2 -> {
                    val raw = token.substring(2)
                    updates["byedpi_ipset"] =
                        if (raw.startsWith(":")) {
                            raw.substring(1)
                        } else {
                            raw
                        }
                }

                token == "-A" && i + 1 < tokens.size -> {
                    val a = tokens[++i]
                    if (a != "none") {
                        updates["byedpi_auto"] = a
                    }
                }
                token.startsWith("-A") && token.length > 2 -> {
                    val a = token.substring(2)
                    if (a != "none") {
                        updates["byedpi_auto"] = a
                    }
                }

                token == "-L" && i + 1 < tokens.size -> {
                    updates["byedpi_auto_mode"] = tokens[++i]
                }
                token.startsWith("-L") && token.length > 2 -> {
                    updates["byedpi_auto_mode"] = token.substring(2)
                }

                token == "-T" && i + 1 < tokens.size -> {
                    updates["byedpi_timeout"] = tokens[++i]
                }
                token.startsWith("-T") && token.length > 2 -> {
                    updates["byedpi_timeout"] = token.substring(2)
                }
            }
            i++
        }

        prefs.edit {
            for ((key, value) in updates) {
                when (value) {
                    is String -> putString(key, value)
                    is Boolean -> putBoolean(key, value)
                    is Int -> putInt(key, value)
                }
            }
        }
    }

    private fun parseOobChar(raw: String): String {
        if (raw.startsWith("\\x") && raw.length >= 4) {
            val hex = raw.substring(2, 4)
            val code = hex.toIntOrNull(16)
            if (code != null) {
                return code.toChar().toString()
            }
        }
        return if (raw.isNotEmpty()) {
            raw.substring(0, 1)
        } else {
            "a"
        }
    }
}
