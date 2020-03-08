package ricochet_robots;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

import java.util.HashMap;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;

public class Case extends CaseCalque implements CaseClickedObservable{

	public static final int DIM = 49;

	private int valHaut;
	private int valDroit;
	private int valBas;
	private int valGauche;

	protected int xCase;
	protected int yCase;

	private ArrayList<CaseClickedObserver> listObserver;

	protected boolean clic = false;

	private int id;

	public Case(int[] tableau, int xCase, int yCase) {
		this(tableau[0], tableau[1], tableau[2], tableau[3], xCase, yCase, true);
	}

	public Case(int valHaut, int valDroit, int valBas, int valGauche, int xCase, int yCase) {
		this(valHaut, valDroit, valBas, valGauche, xCase, yCase, true);
	}

	public Case(int valHaut, int valDroit, int valBas, int valGauche, int xCase, int yCase, boolean loadImage) {
		this.valHaut = valHaut;
		this.valDroit = valDroit;
		this.valBas = valBas;
		this.valGauche = valGauche;
		this.xCase = xCase;
		this.yCase = yCase;


		this.id = Utilitaire.CaseToInt(this);

		this.addImage(new Image("images/imgPlateau/img" + id + ".png"));
		refresh();

		this.setOnMousePressed(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent me){
				//appuyer
				System.out.println("ah§§§§§");
				notifierCaseClique(Case.this);
			}
		});

		this.setOnMouseReleased(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent me){
				//relacher();
				// System.out.println("case relaché!!" );
			}
		});

		listObserver = new ArrayList<>();

	}

	@Override
	public void notifierCaseClique(Case casePlateau){
		for(int i = 0; i < listObserver.size(); i++){
			listObserver.get(i).clicSurCase(this);
		}
	}

	@Override
	public void ajouterObserveurCaseClique(CaseClickedObserver casePlateauObserver){
		listObserver.add(casePlateauObserver);
	}

	@Override
	public void supprimerObserveurCaseClique(CaseClickedObserver casePlateauObserver){
		listObserver.remove(casePlateauObserver);
	}

	public int getValHaut() {
	   return this.valHaut;
	}

	public int getValDroit() {
	   return this.valDroit;
	}

	public int getValBas() {
	   return this.valBas;
	}

	public int getValGauche() {
	   return this.valGauche;
	}

	public int getXCase() {
	   return xCase;
	}

	public int getYCase() {
	   return yCase;
	}

	public int getID() {
	   return id;
	}

	public void setValue(int xCase, int yCase){
		this.xCase = xCase;
		this.yCase = yCase;
		refresh();
	}

	// public Robot contientRobot(ArrayList<Robot> tableauRobots){
	// 	for(int i = 0; i < tableauRobots.size(); i++){
	// 		if(tableauRobots.get(i).getPositionX() == this.xCase && tableauRobots.get(i).getPositionY() == this.yCase){
	// 			return tableauRobots.get(i);
	// 		}
	// 	}
	// 	return null;
	// }


	//Retourne vrai si les valeurs de la case envoyée sont équivalentes à celles qu'on teste
	public boolean isValueEquals(Case caseSelect){
	   return caseSelect.valHaut == valHaut && caseSelect.valBas == valBas && caseSelect.valGauche == valGauche && caseSelect.valDroit == valDroit;
	}

	//fait une rotation de la case, c'est à dire qu'elle décale tout les valeurs da la case d'un cran
	public void rotationCase(){
	   int temp = this.valGauche;
	   this.valGauche = this.valBas;
	   this.valBas = this.valDroit;
	   this.valDroit = this.valHaut;
	   this.valHaut = temp;

	   this.id = Utilitaire.CaseToInt(this);

	   this.addImage(new Image("images/imgPlateau/img" + id + ".png"));

	}

	public void refresh(){
		this.setLayoutX(this.xCase * Case.DIM + Plateau.DEPART_X);
		this.setLayoutY(this.yCase * Case.DIM + Plateau.DEPART_Y);
	}

	//affichage de la Case
	@Override
	public String toString(){
		return "[" + valHaut + "," + valDroit + "," + valBas + "," + valGauche + "]";
	}
}
