package ricochet_robots.jeu.plateau;

import ricochet_robots.jeu.plateau.*;
import ricochet_robots.jeu.observer.*;
import ricochet_robots.utilitaire.*;
import ricochet_robots.jeu.*;

import java.util.Random;
import java.util.ArrayList;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.HashMap;
import javafx.application.Application;
import javafx.scene.input.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;

import javafx.scene.Parent;

public class Plateau extends Parent{

	//La position de départ de dessin du plateau (en pixels)
	public static int DEPART_X = Case.DIM;
	public static int DEPART_Y = Case.DIM;

	//Un tableau de miniGrille
	private ArrayList<Case[][]> tableauMiniGrille = new ArrayList<>();

	//les différentes miniGrilles représentées par une chaine de caractère
	public final String chaine1 = "9,8,8,12,9,8,8,8,1,0,0,0,0,0,0,0,1,0,0,0,0,6,1,0,1,0,2,0,0,8,0,0,3,0,12,1,0,0,0,0,9,2,0,0,0,0,4,3,5,9,0,0,0,0,0,10,1,0,0,0,0,0,4,9"; //haut, gauche
	public final String chaine2 = "8,12,9,8,8,8,8,12,0,0,0,0,0,6,1,6,0,2,0,0,0,8,0,12,4,9,0,0,0,0,2,4,0,0,0,0,0,0,12,5,0,0,0,0,0,0,0,4,2,0,0,4,3,0,0,4,12,1,0,0,8,0,0,4"; //haut droit
	public final String chaine3 = "6,1,0,0,0,0,0,4,8,0,0,0,4,3,0,4,0,0,0,0,0,8,0,4,0,6,1,0,0,0,0,6,0,8,0,0,0,0,2,12,0,0,2,0,0,0,12,5,0,4,9,0,0,0,0,4,2,2,2,6,3,2,2,6"; //bas droit
	public final String chaine4 = "1,0,0,0,0,0,4,3,1,0,0,4,3,0,2,8,3,0,0,0,8,4,9,0,9,0,0,0,0,0,0,2,1,2,0,0,0,0,0,12,1,12,1,0,0,0,0,0,1,0,0,6,1,0,0,0,3,2,2,10,6,3,2,2"; //bas gauche
	public final String[] chaines = {chaine1, chaine2, chaine3, chaine4};

	//Le plateau
	private Case[][] plateau;

	//Zone de positionnement interdite des robots;
	private int[][] deadZone = {{7,7},{8,7},{7,8},{8,8}};

	private Score score;

	public Plateau(int tailleX, int tailleY, Score score){
		//Initialisation du plateau
		 this.plateau = new Case[tailleX][tailleY];

		 this.score = score;

		//Création des miniGrille
		for(int i = 0 ; i < chaines.length; i++){
			creerMiniGrille(chaines[i]);
		}
		//Positionnement des jetons
		positonJeton();

		//Création du plateau
		creerPlateau();

		//Affichage du plateau
		afficheGrille();
	}

	//Récupère une case à une position donnée
	public Case getCasePlateau(int x, int y) {
		if(x >= 0 && x <= plateau.length -1 && y >= 0 && y <= plateau.length -1) {
			return this.plateau[x][y];
		}
		return null;
	}

	//Créer les mini-grilles
	public void creerMiniGrille(String chaine){
		//on ajoute à tableauMiniGrille la miniGrille obtenue avec la méthode stringToMiniGrille
		this.tableauMiniGrille.add(Utilitaire.stringToMiniGrille(chaine));
	}

	//Affichage de la grille
	public void afficheGrille(){
		for(int y = 0; y < plateau[0].length; y++){
			for(int x = 0; x < plateau.length; x++){
				System.out.print(plateau[x][y]);
				this.plateau[x][y].setPositionsXY(x,y);
				this.getChildren().add(plateau[x][y]);
			}
			System.out.println();
		}
	}

	//Ajoute la possibilité que toute les cases soient cliquables
	public void ajoutObserveurCases(CaseClickedObserver observer){
		for(int y = 0; y < plateau[0].length; y++){
			for(int x = 0; x < plateau.length; x++){
				this.plateau[x][y].ajouterObserveurCaseClique(observer);
			}
		}
	}

	//Fait la rotation de la mini-grille en fonction de la position choisie
	public Case[][] rotation(Case[][] miniGrille, int position){
		//On créer un tableau 2D (vide ici), qui représentera une copie de la miniGrille reçue
		Case[][] miniGrilleRota = new Case[miniGrille.length][miniGrille.length];
		//on remplis le tableau miniGrilleRota avec les valeurs de la miniGrille recue
		for(int y = 0; y < miniGrille[0].length; y++){
			for(int x = 0; x < miniGrille.length; x++){
				miniGrilleRota[x][y] = miniGrille[x][y];
			}
		}
		//On fait une rotation de la mini-grille tant qu'elle n'est pas adéquate avec sa position
		while(positionEstVraie(miniGrilleRota, position) != true){
			miniGrilleRota = rotationMiniGrille(miniGrilleRota);
		}
		//on retourne la miniGrille dans sa bonne rotation
		return miniGrilleRota;
	}

	//Fait une rotation de 90° d'un tableau
	public Case[][] rotationMiniGrille(Case[][] miniGrille) {
		Case[][] miniGrilleRota = new Case[miniGrille.length][miniGrille.length];
		//On fait une rotation de la mini-grille
		for(int y = 0; y < miniGrille[0].length; y++){
			for(int x = 0; x < miniGrille.length; x++){
				//On change une valeur de la mini-grille par une autre valeur de mini-grille à un autre endroit
				miniGrilleRota[x][y] = miniGrille[y][miniGrille.length-x-1];
				//On fait une "rotation de la case"
				miniGrilleRota[x][y].rotationCase();
			}
		}
		//On retourne la mini-grille dans une nouvelle rotation
		return miniGrilleRota;
	}

	//Retourne si la mini-grille correspond bien à la position choisie dans le plateau
	public boolean positionEstVraie(Case[][] miniGrille, int position){
		if(position == 1){
			//On vérifie si la mini-grille possède bien des "murs" à gauche et en haut
			return verifGauche(miniGrille) && verifHaut(miniGrille);

		}else if(position == 2){
			//On vérifie si la mini-grille possède bien des "murs" en haut et à droite
			return verifHaut(miniGrille) && verifDroite(miniGrille);

		}else if(position == 3){
			//On vérifie si la mini-grille possède bien des "murs" à droite et en bas
			return verifDroite(miniGrille) && verifBas(miniGrille);

		}else if(position == 4){
			//On vérifie si la mini-grille possède bien des "murs" en bas et à gauche
			return verifBas(miniGrille) && verifGauche(miniGrille);
		}
		return false;
	}

	//Retourne la position de la mini-grille
	public int getPositionMiniGrille(Case[][] miniGrille){
		if(verifGauche(miniGrille) && verifHaut(miniGrille)){
			return 1;

		}else if(verifHaut(miniGrille) && verifDroite(miniGrille)){
			return 2;

		}else if(verifDroite(miniGrille) && verifBas(miniGrille)){
			return 3;

		}else if(verifBas(miniGrille) && verifGauche(miniGrille)){
			return 4;
		}

		return 0;
	}

	//Vérifie si la miniGrille envoyée à bien des murs en haut
	public boolean verifHaut(Case[][] miniGrille){
		boolean ok = false;
		int y = 0;
		//on parcours tout les valeurs x , à y = 0 de la mini-grille
		for(int x = 0; x <miniGrille.length; x++){
			//Avoir un mur en haut, signifie que la valeur valHaut de Case est bien à 1
			if(miniGrille[x][y].getValHaut() == 1){
				ok = true;
			}else{
				return false;
			}
		}
		return ok;
	}

	//Vérifie si la miniGrille envoyée à bien des murs à droite
	public boolean verifDroite(Case[][] miniGrille){
		boolean ok = false;
		int x = miniGrille.length - 1;
		//on parcours à la dernière valeur de x, tout les valeurs de y de la mini-grille
		for(int y = 0; y < miniGrille.length; y++){
			//Avoir un mur à droite, signifie que la valeur valDroit de Case est bien à 1
			if(miniGrille[x][y].getValDroit() == 1){
				ok = true;
			}else{
				return false;
			}
		}
		return ok;
	}

	//Vérifie si la miniGrille envoyée à bien des murs en bas
	public boolean verifBas(Case[][] miniGrille){
		boolean ok = false;
		int y = miniGrille.length - 1;
		//on parcours à la dernière valeur de y, tout les valeurs de x de la mini-grille
		for(int x = 0; x < miniGrille.length; x++){
			//Avoir un mur en bas, signifie que la valeur valBas de Case est bien à 1
			if(miniGrille[x][y].getValBas() == 1){
				ok = true;
			}else{
				return false;
			}
		}
		return ok;
	}

	//Vérifie si la miniGrille envoyée à bien des murs à gauche
	public boolean verifGauche(Case[][] miniGrille){
		boolean ok = false;
		int x = 0;
		//on parcours à x = 0, tout les valeurs de y de la mini-grille
		for(int y = 0; y <miniGrille.length; y++){
			//Avoir un mur à gauche, signifie que la valeur valGauche de Case est bien à 1
			if(miniGrille[x][y].getValGauche() == 1){
				ok = true;
			}else{
				return false;
			}
		}
		return ok;
	}

	//Méthode pour créer le plateau
	public void creerPlateau(){
		//Création d'une ArrayList conteannt la liste des position tirées de manière aléatoire
		ArrayList<Integer> position = new ArrayList<>();
		int alea = 0;
		Random r = new Random();

		//On tire un nombre àléatoire tant qu'on a pas 4 valeurs
		while(position.size() != 4){

			//On tire un nombre entre 1 et 4 (compris)
			alea = r.nextInt(4) + 1;
			//Si notre ArrayList n'a pas cette valeur, on l'ajoute
			if(!position.contains(alea)){
				position.add(alea);
			}
			//..... si non on la perd et on retire

		}

		for(int i = 0; i< tableauMiniGrille.size(); i++){
			/*On modifie la miniGrille correspondante pour qu'elle puisse
			aller dans sa position, choisie de manière aléatoire
			*/
			tableauMiniGrille.set(i, rotation(tableauMiniGrille.get(i), position.get(i)));
			//On ajoute la mini-grille correspondante au plateau
			ajouterMiniGrille(tableauMiniGrille.get(i));
		}
	}

	//Méthode pour ajouter les mini-grilles au plateau
	public void ajouterMiniGrille(Case[][] miniGrille){

		int demiTabX = this.plateau[0].length/2;
		int demiTabY = this.plateau.length/2;

		//si la position de la mini-grille est la première, elle va dans la partie en haut, à gauche du plateau
		if(getPositionMiniGrille(miniGrille) == 1){
			for(int y = 0; y < demiTabY; y++){
				for(int x = 0; x < demiTabX; x++){
					this.plateau[x][y] = miniGrille[x][y];
				}
			}
		}

		//si la position de la mini-grille est la deuxième, elle va dans la partie en haut, à droite du plateau
		else if(getPositionMiniGrille(miniGrille) == 2){
			for(int y = 0; y < demiTabY; y++){
				for(int x = demiTabX; x < this.plateau[0].length ; x++){
					this.plateau[x][y] = miniGrille[x-demiTabX][y];
				}
			}
		}

		//si la position de la mini-grille est la troisième, elle va dans la partie en bas, à droite du plateau
		else if(getPositionMiniGrille(miniGrille) == 3){
			for(int y = demiTabY; y < this.plateau.length ; y++){
				for(int x = demiTabX ; x < this.plateau[0].length; x++){
					this.plateau[x][y] = miniGrille[x-demiTabX][y-demiTabY];
				}
			}
		}

		//si la position de la mini-grille est la quatrième, elle va dans la partie en bas, à gauche du plateau
		else if(getPositionMiniGrille(miniGrille) == 4){
			for(int y = demiTabY; y < this.plateau.length ; y++){
				for(int x = 0; x < demiTabX; x++){
					this.plateau[x][y] = miniGrille[x][y-demiTabX];
					//this.plateau[x][y].setValue(x,y);
				}
			}
		}
	}

	//Positionnement des jetons (pas très propre, à voir comment faire mieux...)
	public void positonJeton(){
		int k=0;
		String[] forme = {"carre","triangle","rond","etoile"};
		String[] couleur = {"rouge","vert","bleu","jaune"};
		int[][] coordonnee = {{2,6,5},{3,3,6},{1,1,3},{0,1,6},{0,7,5},{1,5,1},{2,5,1},{3,4,1},{1,6,4},{0,2,4},{3,6,2},{2,1,3},{3,1,5},{2,2,6},{0,5,2},{1,4,6}};
		for(int i=0; i <= forme.length -1; i++){
			for(int j=0; j <= couleur.length -1; j++){
				tableauMiniGrille.get(coordonnee[k][0])[coordonnee[k][1]][coordonnee[k][2]] = new CaseJeton(tableauMiniGrille.get(coordonnee[k][0])[coordonnee[k][1]][coordonnee[k][2]].getValHaut(), tableauMiniGrille.get(coordonnee[k][0])[coordonnee[k][1]][coordonnee[k][2]].getValDroit(), tableauMiniGrille.get(coordonnee[k][0])[coordonnee[k][1]][coordonnee[k][2]].getValBas(), tableauMiniGrille.get(coordonnee[k][0])[coordonnee[k][1]][coordonnee[k][2]].getValGauche(),i,j, forme[i], couleur[j]);
				k=k+1;
			}
		}
		//tableauMiniGrille.get(3)[7][4] = new CaseJeton(tableauMiniGrille.get(3)[7][4].getValHaut(), tableauMiniGrille.get(3)[7][4].getValDroit(), tableauMiniGrille.get(3)[7][4].getValBas(), tableauMiniGrille.get(3)[7][4].getValGauche(), "spirale", "multi");
	}

	public int[][] getZoneInterdite(){
		return this.deadZone;
	}

	public int getTaillePlateau(){
		return plateau.length;
	}

	public void addGroupPlateau(Parent parent){
		if(!this.getChildren().contains(parent))
			this.getChildren().add(parent);
	}
}