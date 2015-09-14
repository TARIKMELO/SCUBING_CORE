-- Table: municipios_brasil_original

-- DROP TABLE municipios_brasil_original;

-- Sequence: municipios_poligonos_gid_seq

-- DROP SEQUENCE municipios_poligonos_gid_seq;

CREATE SEQUENCE municipios_brasil_10000_gid_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 5563
  CACHE 1;
ALTER TABLE municipios_brasil_10000_gid_seq
  OWNER TO postgres;


CREATE TABLE municipios_brasil_10000
(
  gid integer NOT NULL DEFAULT nextval('municipios_brasil_10000_gid_seq'::regclass),
  cod_ibge character varying(10),
  cod_ufmeso character varying(4),
  nome_meso character varying(35),
  cod_ufmicr character varying(6),
  nome_micro character varying(35),
  cod_uf smallint,
  nome_uf character varying(35),
  nome_muni character varying(45),
  uf character varying(4),
  area numeric,
  geom geometry(MultiPolygon),
  populacao character varying(21),
  pib character varying(21),
  ano integer NOT NULL,
  CONSTRAINT municipios_brasil_10000_pkey PRIMARY KEY (gid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE municipios_brasil_10000
  OWNER TO postgres;

-- Index: municipios_poligonos_geom_idx

-- DROP INDEX municipios_poligonos_geom_idx;

CREATE INDEX municipios_brasil_10000_geom_idx
  ON municipios_brasil_10000
  USING gist
  (geom);

