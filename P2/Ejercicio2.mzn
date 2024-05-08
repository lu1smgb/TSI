include "globals.mzn";

% Definimos los conjuntos de simbolos arbitrarios
enum symbols1 = {a,b,c,d};
enum symbols2 = {w,x,y,z};

% Estructura de datos para almacenar la solucion de correspondencia
array [symbols1] of var symbols2: correspondencia;

% Al ser una correspondencia uno a uno, todos los elementos tendran que ser diferentes
% Es decir, no puede haber varios simbolos del primer grupo que apunten a uno del segundo
constraint all_different(correspondencia);

% Si a no es x, entonces, c no es y
constraint (correspondencia[a] != x) -> (correspondencia[c] != y);

% Si b es y o z, entonces a es x
constraint (correspondencia[b] == y \/ correspondencia[b] == z) -> (correspondencia[a] == x);

% Si c no es w, entonces b es z
constraint (correspondencia[c] != w) -> (correspondencia[b] == z);

% Si d es y, entonces b no es x
constraint (correspondencia[d] == y) -> (correspondencia[b] != x);

% Si d no es x, entonces b es x
constraint (correspondencia[d] != x) -> (correspondencia[b] == x);

% Le pedimos que satisfaga el CSP
solve satisfy;

output [ show(symbol1) ++ " -> " ++ show(correspondencia[symbol1]) ++ "\n" | symbol1 in symbols1];