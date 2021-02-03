# MyWebReviews-An-Android-Collution-demo

## Auteurs
Sergent Adrien
Choveton-Caillat Julien


## Descriptions de l'appli myWebReviews

La page principale est constituée d'un navigateur surplombé par une bannière qui contient la note correspondant au domaine actuel (les étoiles sont vides s'il n'y pas de notes), puis le nom de domaine associé au site actuellement visité et un bouton "go".   
La zone associée au nom de domaine est éditable, on peut y entrer une url et cliquer sur le bouton "go" pour s'y rendre.   
![Premier fragment : navigateur](https://dl.dropboxusercontent.com/s/5jpk7hdx3rxjleo/first_fragment.png?dl=0)   
En cliquant sur la note (les étoiles), et on est envoyé sur le second fragment qui donne accès aux détails de la note que l'on a mise à ce domaine. On peut changer la note qui a été attribuée, et optionnellement ajouter des remarques. Ces changements prennent effet dans la mémoire du téléphone lorsque l'on appuie sur le bouton "send". Enfin il y a un bouton back pour revenir au fragment précédent.   
![Second fragment : Formulaire](https://dl.dropboxusercontent.com/s/c9vllma7yxmxwaz/second_fragment.png?dl=0)   

## Ressources

Le fragment navigateur de myWebReviews est inspiré de par [cet article](https://medium.com/@innocentileka/build-a-simple-web-browser-in-android-studio-7b0b364225ac)   
Le stockage de myWebReviews est fait avec une base de données SQLite, nous sommes parties des bases fournies par [ce tutoriel](https://a-renouard.developpez.com/tutoriels/android/sqlite/)  

## Déroulement de la collution
La collution se fait par un intent explicite lorsque l'usager clique sur "rate this app" dans Simple Contact Viewer. Les contacts sont envoyés à myWebReviews via un champ extra de l'intent. Voyant la présence de ce champ extra, myWebReviews effectue une requête POST au site paste.ee (un site similaire à pastebin avec une api simple d'utilisation) avec ce contenu. L'api de paste.ee renvoie l'url associée au paste qui vient d'être créé, et le navigateur de myWebReviews affiche cette page pour démontrer que la collution a bien eu lieu. On obtient donc ce genre de résultat : 
![Contacts obtenus via collution](https://dl.dropboxusercontent.com/s/lmll41gxl28y2n2/DemoCollutionToPastee.png?dl=0)

## Améliorations
Ces applications ont été pensées comme des **démonstrations** et non comme une tentative d'une collution la plus discrète possible. Tout d'abord, il est facile de détecter la présence d'une autre application grâce aux explicit intents. On aurait donc pu faire en sorte que Simple Contact Viewer lance myWebReviews dès que l'on détecte que cette app a été installée. On pourrait alors faire passer ce comportement pour une popup comme il y en a beaucoup : myWebReviews n'afficherait pas le résultat de la collution sur paste.ee, mais plutôt un site proposant de noter Simple Contact Viewer ou de passer à la version payante...
