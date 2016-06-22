package ar.edu.untref.procesamientoimagenes.modelo;

import android.graphics.Point;

public class Curva {

	private Point desde;
	private Point hasta;

	public Curva(Point desde, Point hasta) {
		this.desde = desde;
		this.hasta = hasta;
	}

	public Point getDesde() {
		return desde;
	}

	public Point getHasta() {
		return hasta;
	}

}
