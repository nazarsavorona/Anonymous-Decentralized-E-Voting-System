-- liquibase formatted sql
-- changeset nazar:1
CREATE TABLE IF NOT EXISTS public.blocks
(
    block_hash character varying UNIQUE NOT NULL,
    major_version integer NOT NULL,
    minor_version integer NOT NULL,
    time_stamp integer NOT NULL,
    previous_block_hash character varying UNIQUE NOT NULL,
    merkle_root_hash character varying NOT NULL,
    nonce integer NOT NULL,

    CONSTRAINT block_id PRIMARY KEY (block_hash)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.blocks
    OWNER to postgres;

CREATE TABLE IF NOT EXISTS public.transactions
(
    tx_id character varying UNIQUE NOT NULL,
    nonce integer NOT NULL,
    candidate_id integer NOT NULL,
    time_stamp integer NOT NULL,
    public_keys character varying NOT NULL,
    signature character varying UNIQUE NOT NULL,
    block_id character varying NOT NULL,

    CONSTRAINT tx_id PRIMARY KEY (tx_id),
    CONSTRAINT block_id_fkey FOREIGN KEY (block_id)
    REFERENCES public.blocks (block_hash) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE CASCADE
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.transactions
    OWNER to postgres;