Girardat Quentin
Bomba Romain

L'application forms permet à un utilisateur de créer des formes simples ou complexes en utilisant des opérations disponibles grâce à une interface graphique. 
On peut également enregistrer localement le dessin ou à distance via RMI. 
Plus particulièrement, la fonctionnalité originale présente est la possibilité de poser directement des préfabriqués (formes complexes). À l'avenir, celle-ci pourra être améliorée en laissant l'utilisateur enregistrer ses propres formes pour les retrouver et pouvoir les disposer directement (la majorité de ces logiciels permettent uniquement le copier coller ce qui peut être embêtant).

Enfin, voici un bug pris en compte lors du développement de l'application il concerne l'imprévisibilité de l'utilisateur. En effet il était possible à un moment donné de réaliser une sélection puis, sans la relâcher de changer de mode par exemple en mode d'ajout de formes. Il a fallu revoir les priorités des listeners pour activer et désactiver les modes au bon moment sans quoi le comportement du logiciel devenait imprévisible. On a également remarqué que plus on rajoutais de modes différents plus on avait de problèmes car ils présentaient de nouvelles failles qui pouvait casser le résultat d'une action qu'on pensait faire dans un mode et pas un autre ou alors liés à la superposition de modes. 