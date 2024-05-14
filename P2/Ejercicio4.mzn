include "globals.mzn";

% Declaracion de las tareas y los trabajadores
enum Tarea = {A, B, C, D, E, F, G, H, I, J};
enum Trabajador = {T1, T2, T3, Apoyo};

% Duracion de las tareas en funcion de los trabajadores (excepto apoyo)
array [Tarea, T1..T3] of int: duracion = [|
  3,6,9 |
  7,5,3 |
  3,2,4 |
  5,5,8 |
  4,3,6 |
  3,2,1 |
  2,2,3 |
  2,4,2 |
  2,3,4 |
  3,2,1 |];

% Que tareas predecen a otras
array [Tarea] of set of Tarea: predecesoras = [
  {}, {A}, {B}, {A,C}, {C}, {C}, {C,D}, {G}, {F,H}, {G,I}
];

% Asignacion final de tareas a los trabajadores
array [Trabajador] of var set of Tarea: asignacion_tareas;

% No se pueden repetir tareas
constraint alldifferent([asignacion_tareas[t] | t in T1..T3]);

% Se realizan todas las tareas
constraint forall(tarea in Tarea)(
  exists(trabajador in T1..T3)(
    exists(tarea_asignada in asignacion_tareas[trabajador])(
      tarea_asignada == tarea
    )
  )
);

% Una tarea no puede realizarse si sus tareas predecesoras no terminaron antes
constraint forall(tarea in Tarea)(
  % Si todos los predecesores han acabado
  if forall(predecesor in predecesoras[tarea])(
    exists(trabajador in T1..T3)(
      exists(tarea_asignada in asignacion_tareas[trabajador])(
        predecesor == tarea_asignada
      )
    )
  )
  % entonces la siguiente tarea tiene que realizarse
  then exists(trabajador in T1..T3)(
    exists(tarea_asignada in asignacion_tareas[trabajador])(
      tarea == tarea_asignada
    )
  )
  endif
);

% El trabajador de apoyo no puede participar en mas de 5 tareas
constraint card(asignacion_tareas[Apoyo]) <= 5;

% Si una tarea dura 2 dias o mas, entonces puede ayudar el trabajador de apoyo
constraint exists(tarea in Tarea)(
  % Si se ha asignado una tarea que dura 2 horas o mas
  if exists(trabajador in T1..T3)(
    exists(tarea_asignada in asignacion_tareas[trabajador])(
      duracion[tarea_asignada, trabajador] >= 2
    )
  ) 
  then
  exists(tarea_asignada_apoyo in asignacion_tareas[Apoyo])(
    tarea == tarea_asignada_apoyo
  )
  endif
);

% Tiempo (dias) que se tarda en realizar todas las tareas
var int: duracion_total = sum(trabajador in T1..T3)(
  sum(tarea in asignacion_tareas[trabajador])(
    % Si hay una tarea en la que ayuda el trabajador de apoyo se reduce la duracion de la tarea
    if exists(tarea_apoyo in asignacion_tareas[Apoyo])(tarea == tarea_apoyo) then
      duracion[tarea, trabajador] - 1
    else
      duracion[tarea, trabajador]
    endif
  )
);
% Version sin tener en cuenta al trabajador de apoyo
% var int: duracion_total = sum(trabajador in T1..T3)(
%   sum(tarea in asignacion_tareas[trabajador])(
%     duracion[tarea, trabajador]
%   )
% );

% Resolucion y output
solve minimize duracion_total;

output ["El proyecto terminara en " ++ show(duracion_total) ++ " dias\n"];
output [show(trabajador) ++ " -> " ++ show(asignacion_tareas[trabajador]) ++ if trabajador != Apoyo then "\n" else "" endif | trabajador in Trabajador];