package ricochet_robots;

import java.util.ArrayList;
import java.util.Random;

import javafx.scene.input.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

import java.util.HashMap;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.scene.Parent;

public class Robot extends Parent implements RobotClickedObservable{

	Random r = new Random();

	private String couleur;
	private int positionInitialeX;
	private int positionInitialeY;
	private int positionX;
	private int positionY;
	private State etatJeu;

	private ImageView imageRobot;
	private ImageView imageSocle;

	private ArrayList<RobotClickedObserver> listObserver;

	public Robot(State etatJeu, String couleur, int positionX, int positionY){
		//On ajoute le robot crée à la liste des robots
		State.tableauRobots.add(this);
		this.couleur = couleur;
		this.positionInitialeX = positionX;
		this.positionInitialeY = positionY;
		this.positionX = positionX;
		this.positionY = positionY;
		this.etatJeu = etatJeu;

		dessinerSocleRobot();
		dessinerRobot();

		listObserver = new ArrayList<>();

		//Evènement clic sur le robot
		this.setOnMousePressed(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent me){
				notifierRobotClique(Robot.this);
			}
		});
	}

	@Override
	public void notifierRobotClique(Robot robot){
		for(int i = 0; i < listObserver.size(); i++){
			etatJeu.getEtatPlateau().clicSurRobot(robot);
		}
	}

	@Override
	public void ajouterObserveurRobotClique(RobotClickedObserver robotObserver){
		listObserver.add(robotObserver);
	}

	@Override
	public void supprimerObserveurRobotClique(RobotClickedObserver robotObserver){
		listObserver.remove(robotObserver);
	}

	public int getPositionInitialeX(){
		return positionInitialeX;
	}

	public void setPositionInitialeX(int positionInitialeX){
		this.positionInitialeX = positionInitialeX;
	}

	public int getPositionInitialeY(){
		return positionInitialeY;
	}

	public void setPositionInitialeY(int positionInitialeY){
		this.positionInitialeY = positionInitialeY;
	}

	public int getPositionX(){
		return positionX;
	}

	public void setPositionX(int positionX){
		this.positionX = positionX;
	}

	public int getPositionY(){
		return positionY;
	}

	public void setPositionY(int positionY){
		this.positionY = positionY;
	}

	public void setPosition(int x, int y){
		this.positionY = positionY;
		this.positionX = positionX;
	}

	public String getCouleur(){
		return this.couleur;
	}

	public void setCouleur(String couleur){
		this.couleur = couleur;
	}

	//Vérifie si deux robots sont en collisions
	public static boolean estSurAutresRobots(int aleaX, int aleaY){
		for(int i = 0; i <= State.tableauRobots.size() -1 ; i++){
			//Vérifie si les coordonnées X et Y tirées sont déja affectés a un robot déja crée
			if(aleaX == State.tableauRobots.get(i).getPositionInitialeX() && aleaY == State.tableauRobots.get(i).getPositionInitialeY()){
				//Si c'est le cas, on retrourne vrai
				return true;
			}
		}
		//Si ce n'est pas le cas, on retrourne false
		return false;
	}

	public boolean estUneCollisionRobot(Deplacement direction){
		switch(direction){
			case UP:
				for(int i = 0; i< State.tableauRobots.size(); i++){
					if(State.tableauRobots.get(i).getCouleur() != couleur && State.tableauRobots.get(i).getPositionX() == positionX &&  State.tableauRobots.get(i).getPositionY() == (positionY - 1)){
						return true;
					}
				}
				return false;

			case DOWN:
				for(int i = 0; i< State.tableauRobots.size(); i++){
					if(State.tableauRobots.get(i).getCouleur() != couleur && State.tableauRobots.get(i).getPositionX() == positionX &&  State.tableauRobots.get(i).getPositionY() == (positionY + 1)){
						return true;
					}
				}
				return false;

			case LEFT:
				for(int i = 0; i< State.tableauRobots.size(); i++){
					if(State.tableauRobots.get(i).getCouleur() != couleur && State.tableauRobots.get(i).getPositionX() == (positionX - 1)  &&  State.tableauRobots.get(i).getPositionY() == positionY){
						return true;
					}
				}
				return false;

			case RIGHT:
				for(int i = 0; i< State.tableauRobots.size(); i++){
					if(State.tableauRobots.get(i).getCouleur() != couleur && State.tableauRobots.get(i).getPositionX() == (positionX + 1)  &&  State.tableauRobots.get(i).getPositionY() == positionY){
						return true;
					}
				}
				return false;
		}
		return true;
	}

	//Permet de savoir quel robot doit jouer
	public boolean estRobotAJouer(String couleurTire){
		if(this.couleur.equals(couleurTire)){
			return true;
		}
		return false;
	}

	//Vérifie si il y a un robot sur une case
	public boolean caseAvecRobot(int xCase, int yCase){
		return xCase == this.positionX && yCase == this.positionY;
	}

	//Déplacement du robot
	public void move(Deplacement direction){

		//Vérification de la direction choisie
		if(direction == Deplacement.UP){
			//Tant que le robot ne rencontre pas un mur en haut, ou un autre robot, il se dirige vers le haut
			while(this.etatJeu.getEtatPlateau().getCasePlateau(positionX, positionY).getValHaut() != 1 && this.etatJeu.getEtatPlateau().getCasePlateau(positionX, positionY - 1).getValBas() != 1 && !estUneCollisionRobot(direction)){
				this.positionY -= 1;
			}
		}else if(direction == Deplacement.DOWN){
			//Tant que le robot ne rencontre pas un mur en bas, ou un autre robot, il se dirige vers le bas
			while(this.etatJeu.getEtatPlateau().getCasePlateau(positionX, positionY).getValBas() != 1 && this.etatJeu.getEtatPlateau().getCasePlateau(positionX, positionY +1).getValHaut() != 1 && !estUneCollisionRobot(direction)){
				this.positionY += 1;
			}
		}else if(direction == Deplacement.LEFT){
			//Tant que le robot ne rencontre pas un mur à gauche,  ou un autre robot, il se dirige vers la gauche
			while(this.etatJeu.getEtatPlateau().getCasePlateau(positionX, positionY).getValGauche() != 1 && this.etatJeu.getEtatPlateau().getCasePlateau(positionX -1, positionY).getValDroit() != 1&& !estUneCollisionRobot(direction)){
				this.positionX -= 1;
			}
		}else if(direction == Deplacement.RIGHT){
			//Tant que le robot ne rencontre pas un mur à droite,  ou un autre robot, il se dirige vers la droite
			while(this.etatJeu.getEtatPlateau().getCasePlateau(positionX, positionY).getValDroit() != 1 && this.etatJeu.getEtatPlateau().getCasePlateau(positionX + 1, positionY).getValGauche() != 1  && !estUneCollisionRobot(direction)){
				this.positionX += 1;
			}
		}
	}

	public void nouvellePositionSocle(){
		this.positionInitialeX = this.positionX;
		this.positionInitialeY = this.positionY;
		refreshPosSocleRobot();
	}

	public void reinitialiserPosition(){
		this.positionX = this.positionInitialeX;
		this.positionY = this.positionInitialeY;
		refreshPosRobot();

	}

	//On dessine le robot
	public void dessinerRobot(){
		this.imageRobot = new ImageView(new Image("images/imgRobot/" + this.couleur + ".png"));
		this.getChildren().add(this.imageRobot);
		refreshPosRobot();
	}

	//On dessine la "socle" du robot
	public void dessinerSocleRobot(){
		this.imageSocle = new ImageView(new Image("images/imgSocleRobot/" + this.couleur + ".png"));
		this.getChildren().add(this.imageSocle);
		refreshPosSocleRobot();
	}

	//Mise à jour des positionnements des dessins du robot
	public void refreshPosRobot(){
		this.imageRobot.setX(this.positionX * Case.DIM + Plateau.DEPART_X);
		this.imageRobot.setY(this.positionY * Case.DIM + Plateau.DEPART_X);
	}

	//Mise à jour des positionnements des dessins du robot
	public void refreshPosSocleRobot(){
		this.imageSocle.setX(this.positionInitialeX * Case.DIM + Plateau.DEPART_X);
		this.imageSocle.setY(this.positionInitialeY * Case.DIM + Plateau.DEPART_X);
		System.out.println("robot " + couleur + " : socleX:" + this.positionInitialeX + " socleY: " + this.positionInitialeY );
	}
}
