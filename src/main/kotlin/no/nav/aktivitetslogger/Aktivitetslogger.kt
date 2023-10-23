package no.sjovatsen.aktivitetslogger

import mu.KotlinLogging
import no.nav.aktivitetslogger.AktivitetsloggConfig
import no.nav.aktivitetslogger.Configuration.config
import no.nav.aktivitetslogger.Hendelse
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.KafkaRapid
import java.time.LocalDateTime

typealias aktivitetslogger = Aktivitetslogger

object Aktivitetslogger {

    private val logger = KotlinLogging.logger { }

    private val kafkaRapid: KafkaRapid = KafkaRapid.create(
        AktivitetsloggConfig.fromEnv(config),
        config.getValue("KAFKA_RAPID_TOPIC"),
    )

    init {
        logger.info { "Starter aktivitetslogg" }
    }

    fun info(hendelse: Hendelse) {
        send(hendelse, Alvorlighetsgrad.INFO, null)
    }

    fun info(hendelse: Hendelse, aktivitet: Aktivitet?) {
        send(hendelse, Alvorlighetsgrad.INFO, aktivitet)
    }

    fun advarsel(hendelse: Hendelse) {
        send(hendelse, Alvorlighetsgrad.ADVARSEL, null)
    }
    fun advarsel(hendelse: Hendelse, aktivitet: Aktivitet?) {
        send(hendelse, Alvorlighetsgrad.ADVARSEL, aktivitet)
    }

    private fun send(hendelse: Hendelse, alvorlighetsgrad: Alvorlighetsgrad, aktivitet: Aktivitet?) {
        logger.info { "publishing aktivitetslogg event for meldingsreferanse=${hendelse.meldingsreferanseId()}" }
        kafkaRapid.publish(
            JsonMessage.newMessage(
                "aktivitetslogg",
                mapOf(
                    "hendelse" to mapOf(
                        "type" to hendelse.javaClass.simpleName,
                        "meldingsreferanseId" to hendelse.meldingsreferanseId(),
                    ),
                    "ident" to hendelse.ident(),
                    "aktivitet" to if (aktivitet != null) {
                        mapOf(
                            "kontekster" to aktivitet.kontekst(),
                            "alvorlighetsGrad" to alvorlighetsgrad,
                            "melding" to aktivitet.melding(),
                            "tidsstempel" to LocalDateTime.now(),
                        )
                    } else {
                        emptyMap<String, Any>()
                    },
                ),
            ).toJson(),
        )
    }
}
