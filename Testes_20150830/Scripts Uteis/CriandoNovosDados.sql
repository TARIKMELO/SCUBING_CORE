INSERT INTO municipios_brasil_10000(
  gid ,
  cod_ibge ,
  cod_ufmeso,
  nome_meso ,
  cod_ufmicr ,
  nome_micro,
  cod_uf,
  nome_uf,
  nome_muni ,
  uf,
  area ,
  geom ,
  populacao ,
  pib ,
  ano )

select 
  NEXTVAL('municipios_brasil_10000_gid_seq') ,
  cod_ibge ,
  cod_ufmeso,
  nome_meso ,
  cod_ufmicr ,
  nome_micro,
  cod_uf,
  nome_uf,
  nome_muni ,
  uf,
  area ,
  geom ,
  populacao ,
  pib ,
  2011 

  from 

  municipios_brasil_original

 ;
