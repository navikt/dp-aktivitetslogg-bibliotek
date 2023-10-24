package no.nav.aktivitetslogger

import io.kotest.matchers.equals.shouldBeEqual
import no.nav.dagpenger.aktivitetslogger.Aktivitet
import no.nav.dagpenger.aktivitetslogger.Aktivitetslogger
import no.nav.dagpenger.aktivitetslogger.Alvorlighetsgrad
import no.nav.dagpenger.aktivitetslogger.Kontekst
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.util.UUID

class AktivitetsloggerTest {
    companion object {
        private var rapid: TestRapid = TestRapid()
    }

    private val aktivitetslogger: Aktivitetslogger = Aktivitetslogger.logger(rapid)

    private val hendelse = TestHendelse(UUID.randomUUID(), "11111111111")
    private val aktivitet = Aktivitet(
        melding = "Hva skjer?",
        kontekst = listOf(
            Kontekst(
                type = "Person",
                map = mapOf(
                    "ident" to hendelse.ident(),
                ),
            ),
            Kontekst(
                type = "Person",
                map = mapOf(
                    "ident" to hendelse.ident(),
                ),
            ),
        ),
    )

    @AfterEach
    fun cleanup() {
        rapid.reset()
    }

    @Test
    fun `Instansen av Aktivitetsloggeren skal være den samme hver gang man henter loggeren`() {
        val aktivitetslogger1 = Aktivitetslogger.logger(rapid)
        val aktivitetslogger2 = Aktivitetslogger.logger(rapid)

        aktivitetslogger2.shouldBeEqual(aktivitetslogger1)
    }

    @Test
    fun `Info melding skal ha alvorlighetsgrad INFO`() {
        aktivitetslogger.info(
            hendelse,
            aktivitet,
        )

        rapid.inspektør.size.shouldBeEqual(1)
        rapid.inspektør
            .message(0)
            .get("aktivitet")
            .get("alvorlighetsGrad")
            .asText() shouldBeEqual Alvorlighetsgrad.INFO.toString()
    }

    @Test
    fun `Advarsel melding skal ha alvorlighetsgrad ADVARSEL`() {
        aktivitetslogger.advarsel(
            hendelse,
            aktivitet,
        )

        rapid.inspektør.size shouldBeEqual 1
        rapid.inspektør.message(0)
            .get("aktivitet")
            .get("alvorlighetsGrad")
            .asText() shouldBeEqual Alvorlighetsgrad.ADVARSEL.toString()
    }

    @Test
    fun `Behov melding skal ha alvorlighetsgrad BEHOV`() {
        aktivitetslogger.behov(
            hendelse,
            aktivitet,
        )

        rapid.inspektør.size shouldBeEqual 1
        rapid.inspektør
            .message(0)
            .get("aktivitet")
            .get("alvorlighetsGrad")
            .asText() shouldBeEqual Alvorlighetsgrad.BEHOV.toString()
    }

    @Test
    fun `Feil melding skal ha alvorlighetsgrad FEIL`() {
        aktivitetslogger.feil(
            hendelse,
            aktivitet,
        )

        rapid.inspektør.size shouldBeEqual 1
        rapid.inspektør
            .message(0)
            .get("aktivitet")
            .get("alvorlighetsGrad")
            .asText() shouldBeEqual Alvorlighetsgrad.FEIL.toString()
    }

    @Test
    fun `Alvorlig melding skal ha alvorlighetsgrad ALVORLIG`() {
        aktivitetslogger.alvorlig(
            hendelse,
            aktivitet,
        )

        rapid.inspektør.size shouldBeEqual 1
        rapid.inspektør
            .message(0)
            .get("aktivitet")
            .get("alvorlighetsGrad")
            .asText() shouldBeEqual Alvorlighetsgrad.ALVORLIG.toString()
    }
}
