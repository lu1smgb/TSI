include "globals.mzn";

% Definimos los colores y los costes de cada color
enum Color = {Verde, Rojo, Naranja, Azul};
array [Color] of int: coste_color = [100, 250, 350, 450];

% Definimos una enumeracion para los paises, asi como dos arrays para almacenar sus nombres y sus vecinos
enum Pais = {ARG, BOL, BRA, CHI, COL, ECU, GUA, GUY, PAR, PER, SUR, URU, VEN};
array [Pais] of string: nombre_pais = [
  "Argentina",
  "Bolivia",
  "Brasil",
  "Chile",
  "Colombia",
  "Ecuador",
  "Guayana Francesa",
  "Guyana",
  "Paraguay",
  "Peru",
  "Surinam",
  "Uruguay",
  "Venezuela"
];
array [Pais] of set of Pais: vecinos = [
  {BOL, BRA, CHI, PAR, URU},
  {ARG, BRA, CHI, PAR, PER},
  {ARG, BOL, COL, GUA, GUY, PAR, PER, SUR, URU, VEN},
  {ARG, BOL, PER},
  {BRA, ECU, PER, VEN},
  {COL, PER},
  {BRA, SUR},
  {BRA, SUR, VEN},
  {ARG, BOL, BRA, URU},
  {BOL, BRA, CHI, COL, ECU},
  {BRA, GUA, GUY},
  {ARG, BRA, PAR},
  {BRA, COL, GUY}
];

% Definimos la estructura de datos para almacenar el coloreado de los paises
array [Pais] of var Color: color_asignado;

% Restriccion: para cada pais p, que sus vecinos tengan un color asignado diferente al de p
constraint forall(p in Pais)( forall(v in vecinos[p])( color_asignado[p] != color_asignado[v] ));

% COP: Declaramos el coste total
var int: coste_total = sum(p in Pais)(coste_color[color_asignado[p]]);

solve minimize coste_total; % COP

output ["total: " ++ show(coste_total) ++ "\n\n"];
output [nombre_pais[p] ++ ": " ++ show(color_asignado[p]) ++ "\t" ++ show(coste_color[color_asignado[p]]) ++ "\n" | p in Pais];