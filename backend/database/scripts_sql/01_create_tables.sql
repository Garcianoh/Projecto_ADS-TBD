CREATE TABLE local (
    id_local  NUMBER GENERATED ALWAYS AS IDENTITY,
    nome      VARCHAR2(150) NOT NULL,
    latitude  NUMBER(10, 7) NOT NULL,
    longetude NUMBER(10, 7) NOT NULL,
    timestamp TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT Pk_local PRIMARY KEY (id_local),
    CONSTRAINT Uq_local_nome UNIQUE (nome)
);

CREATE TABLE curso (
    id_curso  NUMBER GENERATED ALWAYS AS IDENTITY,
    nome      VARCHAR2(150) NOT NULL,
    timestamp TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT Pk_curso PRIMARY KEY (id_curso)
);

CREATE TABLE categoria (
    id_categoria NUMBER GENERATED ALWAYS AS IDENTITY,
    titulo       VARCHAR2(100) NOT NULL,
    timestamp    TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT Pk_categoria PRIMARY KEY (id_categoria)
);

CREATE TABLE utente (
    id_utente     NUMBER GENERATED ALWAYS AS IDENTITY,
    nome          VARCHAR2(150) NOT NULL,
    apelido       VARCHAR2(150) NOT NULL,
    nick          VARCHAR2(50)  NOT NULL,
    numero_utente VARCHAR2(50)  NOT NULL,
    email         VARCHAR2(150) NOT NULL,
    password      VARCHAR2(150) NOT NULL,
    foto_url      VARCHAR2(500),
    timestamp     TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    id_categoria  NUMBER        NOT NULL,
    CONSTRAINT Pk_utente PRIMARY KEY (id_utente),
    CONSTRAINT Uq_utente_numero UNIQUE (numero_utente),
    CONSTRAINT Uq_utente_email  UNIQUE (email),
    CONSTRAINT Uq_utente_nick   UNIQUE (nick),
    CONSTRAINT Fk_utente_categoria FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)
);

CREATE TABLE aluno (
    id_aluno  NUMBER GENERATED ALWAYS AS IDENTITY,
    id_curso  NUMBER NOT NULL,
    id_utente NUMBER NOT NULL,
    timestamp TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT Pk_aluno PRIMARY KEY (id_aluno),
    CONSTRAINT Uq_aluno_utente UNIQUE (id_utente),
    CONSTRAINT Fk_aluno_curso  FOREIGN KEY (id_curso) REFERENCES curso (id_curso),
    CONSTRAINT Fk_aluno_utente FOREIGN KEY (id_utente) REFERENCES utente (id_utente)
);

CREATE TABLE conta (
    id_conta  NUMBER GENERATED ALWAYS AS IDENTITY,
    saldo     NUMBER(15,2) DEFAULT 0 NOT NULL,
    id_utente NUMBER NOT NULL,
    timestamp TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT Pk_conta PRIMARY KEY (id_conta),
    CONSTRAINT Uq_conta_utente UNIQUE (id_utente),
    CONSTRAINT Chk_conta_saldo CHECK (saldo >= 0),
    CONSTRAINT Fk_conta_utente FOREIGN KEY (id_utente) REFERENCES utente (id_utente)
);

CREATE TABLE viatura (
    id_viatura NUMBER GENERATED ALWAYS AS IDENTITY,
    nome       VARCHAR2(100) NOT NULL,
    modelo     VARCHAR2(100) NOT NULL,
    matricula  VARCHAR2(20)  NOT NULL,
    capacidade NUMBER(3)    NOT NULL,
    id_utente  NUMBER        NOT NULL,
    timestamp  TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT Pk_viatura PRIMARY KEY (id_viatura),
    CONSTRAINT Uq_viatura_matricula   UNIQUE (matricula),
    CONSTRAINT Chk_viatura_capacidade CHECK (capacidade >= 2),
    CONSTRAINT Fk_viatura_utente FOREIGN KEY (id_utente) REFERENCES utente (id_utente)
);

CREATE TABLE trajeto (
    id_trajeto       NUMBER GENERATED ALWAYS AS IDENTITY,
    id_local_origem  NUMBER NOT NULL,
    id_local_destino NUMBER NOT NULL,
    timestamp        TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT Pk_trajeto PRIMARY KEY (id_trajeto),
    CONSTRAINT Chk_trajeto_locais CHECK (id_local_origem <> id_local_destino),
    CONSTRAINT Fk_trajeto_origem FOREIGN KEY (id_local_origem) REFERENCES local (id_local),
    CONSTRAINT Fk_trajeto_destino FOREIGN KEY (id_local_destino) REFERENCES local (id_local)
);

CREATE TABLE boleia (
    id_boleia   NUMBER GENERATED ALWAYS AS IDENTITY,
    custo       NUMBER(10, 2) NOT NULL,
    data_inicio DATE          NOT NULL,
    tipo_boleia VARCHAR2(20)  NOT NULL,
    estado      VARCHAR2(10)  DEFAULT 'ATIVO' NOT NULL,
    id_trajeto  NUMBER        NOT NULL,
    timestamp   TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT Pk_boleia PRIMARY KEY (id_boleia),
    CONSTRAINT Chk_boleia_custo  CHECK (custo >= 0),
    CONSTRAINT Chk_boleia_tipo   CHECK (tipo_boleia IN ('UNICA', 'DIARIA', 'SEMANAL', 'MENSAL')),
    CONSTRAINT Chk_boleia_estado CHECK (estado IN ('ATIVO', 'INATIVO')),
    CONSTRAINT Fk_boleia_trajeto FOREIGN KEY (id_trajeto) REFERENCES trajeto (id_trajeto)
);

CREATE TABLE frequencia (
    id_frequencia   NUMBER GENERATED ALWAYS AS IDENTITY,
    data_fim        DATE         NULL,
    tipo_frequencia VARCHAR2(20) NOT NULL,
    id_boleia       NUMBER       NOT NULL,
    timestamp       TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT Pk_frequencia_boleia  PRIMARY KEY (id_frequencia),
    CONSTRAINT Uq_frequencia_boleia  UNIQUE (id_boleia),
    CONSTRAINT Chk_frequencia_tipo   CHECK (tipo_frequencia IN ('DIARIA', 'SEMANAL', 'MENSAL')),
    CONSTRAINT Fk_frequencia_boleia  FOREIGN KEY (id_boleia) REFERENCES boleia (id_boleia)
);

CREATE TABLE utente_boleia (
    id_utente_boleia NUMBER GENERATED ALWAYS AS IDENTITY,
    tipo_utente      VARCHAR2(20) NOT NULL,
    id_utente        NUMBER       NOT NULL,
    id_boleia        NUMBER       NOT NULL,
    timestamp        TIMESTAMP  DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT Pk_utente_boleia PRIMARY KEY (id_utente_boleia),
    CONSTRAINT Uq_utente_boleia UNIQUE (id_utente, id_boleia),
    CONSTRAINT Chk_utente_boleia_tipo CHECK(tipo_utente IN ('PASSAGEIRO', 'CONDUTOR')),
    CONSTRAINT Fk_ub_utente FOREIGN KEY (id_utente) REFERENCES utente (id_utente),
    CONSTRAINT Fk_ub_boleia FOREIGN KEY (id_boleia) REFERENCES boleia (id_boleia)
);

CREATE TABLE transacao_pagamento (
    id_transacao  NUMBER GENERATED ALWAYS AS IDENTITY,
    id_conta      NUMBER          NOT NULL,
    valor         NUMBER(15,2)    NOT NULL,
    referencia    VARCHAR2(50),
    tipo          VARCHAR2(20)    NOT NULL,
    estado        VARCHAR2(20)    DEFAULT 'PENDENTE' NOT NULL,
    timestamp     TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT Pk_transacao         PRIMARY KEY (id_transacao),
    CONSTRAINT Uq_transacao_ref     UNIQUE (referencia),
    CONSTRAINT Chk_transacao_valor  CHECK (valor > 0),
    CONSTRAINT Chk_transacao_tipo   CHECK (tipo IN ('DIRECTO', 'REFERENCIA')),
    CONSTRAINT Chk_transacao_estado CHECK (estado IN ('PENDENTE', 'CONFIRMADO', 'CANCELADO')),
    CONSTRAINT Fk_transacao_conta   FOREIGN KEY (id_conta) REFERENCES conta (id_conta)
);