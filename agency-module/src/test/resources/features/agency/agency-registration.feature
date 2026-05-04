# language: fr
Fonctionnalité: Enregistrement d'une agence partenaire JEMIL

  En tant que responsable JEMIL
  Je veux enregistrer des agences de transport partenaires
  Afin qu'elles puissent proposer leurs billets sur la plateforme

  Contexte:
    Etant donné que le système JEMIL est opérationnel

  Scénario: Enregistrement réussi d'une agence
    Quand j'enregistre une agence avec les informations suivantes:
      | champ         | valeur           |
      | nom           | Global Voyages   |
      | ville         | Douala           |
      | telephone     | +237655000001    |
    Alors l'agence est créée avec le statut "ACTIVE"
    Et l'agence possède un identifiant unique
    Et je reçois une réponse HTTP 201

  Scénario: Refus d'une agence avec un nom vide
    Quand j'enregistre une agence avec les informations suivantes:
      | champ         | valeur   |
      | nom           |          |
      | ville         | Yaoundé  |
      | telephone     |          |
    Alors je reçois une réponse HTTP 400
    Et le message d'erreur contient "nom"

  Scénario: Récupération d'une agence existante
    Etant donné qu'une agence "Trésor Voyages" existe dans la ville "Bafoussam"
    Quand je récupère l'agence par son identifiant
    Alors je reçois une réponse HTTP 200
    Et la réponse contient le nom "Trésor Voyages"

  Scénario: Récupération des agences par ville
    Etant donné qu'une agence "Buca Voyages" existe dans la ville "Douala"
    Et qu'une agence "Blue Bird Express" existe dans la ville "Yaoundé"
    Quand je récupère les agences de la ville "Douala"
    Alors je reçois une réponse HTTP 200
    Et la liste contient 1 agence
    Et la liste contient l'agence "Buca Voyages"
