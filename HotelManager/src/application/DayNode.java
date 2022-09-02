package application;

import java.time.LocalDate;

import javafx.geometry.Pos;
import javafx.scene.control.Label;

public class DayNode  extends Label{
	
    private LocalDate date;
    private boolean busy = false;

    
    public DayNode() {
        super();
        this.setAlignment(Pos.CENTER);
        this.setPrefHeight(80);
        this.setPrefWidth(80);
        this.getStyleClass().add("costum");

    }
    
    public boolean getBusy() {
		return busy;
	}

    public LocalDate getDate() {
        return date;
    }

	public void setBusy(boolean busy) {
		this.busy = busy;
	}

	public void setDate(LocalDate date) {
        this.date = date;
    }
}
