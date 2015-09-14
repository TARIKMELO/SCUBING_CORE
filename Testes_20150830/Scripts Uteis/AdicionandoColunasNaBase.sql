update municipios_poligonos set pib = municipios_2010.pib from municipios_2010
where municipios_poligonos.cod_ibge = municipios_2010.codigo_ibg;


update municipios_poligonos set populacao = municipios_2010.populacao from municipios_2010
where municipios_poligonos.cod_ibge = municipios_2010.codigo_ibg;


update municipios_poligonos set populacao = 0 where populacao is null;


update municipios_poligonos set pib = 0 where pib is null;