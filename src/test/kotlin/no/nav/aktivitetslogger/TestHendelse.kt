package no.nav.aktivitetslogger

import no.nav.dagpenger.aktivitetslogger.Hendelse
import java.util.UUID

class TestHendelse(meldingsreferanseId: UUID, ident: String) : Hendelse(meldingsreferanseId, ident)
