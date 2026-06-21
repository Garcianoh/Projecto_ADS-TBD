-- =============================================================
-- trg_criar_conta_utente
-- Cria automaticamente uma conta (saldo 0) sempre que um novo
-- utente é inserido na tabela utente.
-- =============================================================

CREATE OR REPLACE TRIGGER trg_criar_conta_utente
AFTER INSERT ON utente
FOR EACH ROW
BEGIN
    INSERT INTO conta (saldo, id_utente)
    VALUES (0, :NEW.id_utente);
END trg_criar_conta_utente;
/