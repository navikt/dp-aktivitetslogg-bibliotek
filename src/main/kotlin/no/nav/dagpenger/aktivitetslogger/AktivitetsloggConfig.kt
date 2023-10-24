package no.nav.dagpenger.aktivitetslogger

import no.nav.helse.rapids_rivers.KafkaConfig
import java.io.File
import java.io.FileNotFoundException
import java.net.InetAddress
import java.util.UUID

internal class AktivitetsloggConfig {
    companion object {
        fun fromEnv(env: Map<String, String>) = kafkaConfig(env, generateInstanceId(env))

        private fun kafkaConfig(env: Map<String, String>, instanceId: String): KafkaConfig {
            val preferOnPrem = env["KAFKA_PREFER_ON_PREM"]?.let { it.lowercase() == "true" } ?: false
            if (preferOnPrem || !gcpConfigAvailable(env)) {
                return onPremConfig(
                    env,
                    instanceId,
                )
            }
            return gcpConfig(env, instanceId)
        }

        private fun gcpConfigAvailable(env: Map<String, String>) =
            env.containsKey("KAFKA_BROKERS") && env.containsKey("KAFKA_CREDSTORE_PASSWORD")

        private fun gcpConfig(env: Map<String, String>, instanceId: String) =
            KafkaConfig(
                bootstrapServers = env.getValue("KAFKA_BROKERS"),
                consumerGroupId = env.getValue("KAFKA_CONSUMER_GROUP_ID"),
                clientId = instanceId,
                username = null,
                password = null,
                truststore = env["KAFKA_TRUSTSTORE_PATH"],
                truststorePassword = env.getValue("KAFKA_CREDSTORE_PASSWORD"),
                keystoreLocation = env.getValue("KAFKA_KEYSTORE_PATH"),
                keystorePassword = env.getValue("KAFKA_CREDSTORE_PASSWORD"),
                autoOffsetResetConfig = env["KAFKA_RESET_POLICY"],
                autoCommit = env["KAFKA_AUTO_COMMIT"]?.let { "true" == it.lowercase() },
                maxIntervalMs = env["KAFKA_MAX_POLL_INTERVAL_MS"]?.toInt(),
                maxRecords = env["KAFKA_MAX_RECORDS"]?.toInt(),
            )

        private fun onPremConfig(env: Map<String, String>, instanceId: String) =
            KafkaConfig(
                bootstrapServers = env.getValue("KAFKA_BOOTSTRAP_SERVERS"),
                consumerGroupId = env.getValue("KAFKA_CONSUMER_GROUP_ID"),
                clientId = instanceId,
                username = "/var/run/secrets/nais.io/service_user/username".readFile(),
                password = "/var/run/secrets/nais.io/service_user/password".readFile(),
                truststore = env["NAV_TRUSTSTORE_PATH"],
                truststorePassword = env["NAV_TRUSTSTORE_PASSWORD"],
                keystoreLocation = null,
                keystorePassword = null,
                autoOffsetResetConfig = env["KAFKA_RESET_POLICY"],
                autoCommit = env["KAFKA_AUTO_COMMIT"]?.let { "true" == it.lowercase() },
                maxIntervalMs = env["KAFKA_MAX_POLL_INTERVAL_MS"]?.toInt(),
                maxRecords = env["KAFKA_MAX_RECORDS"]?.toInt(),
            )

        private fun generateInstanceId(env: Map<String, String>): String {
            if (env.containsKey("NAIS_APP_NAME")) return InetAddress.getLocalHost().hostName
            return UUID.randomUUID().toString()
        }
    }
}

private fun String.readFile() =
    try {
        File(this).readText(Charsets.UTF_8)
    } catch (err: FileNotFoundException) {
        null
    }
