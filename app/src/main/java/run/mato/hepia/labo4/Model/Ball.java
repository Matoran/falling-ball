package run.mato.hepia.labo4.Model;

import android.graphics.Color;
import android.graphics.RectF;

/**
 * Created by matoran on 1/6/18.
 */

public class Ball {
    // Permet à la boule d'accélérer moins vite
    private static final float COMPENSATEUR = 8.0f;
    // Utilisé pour compenser les rebonds
    private static final float REBOND = 1.75f;
    // Rayon de la boule
    public static float RADIUS;
    // Vitesse maximale autorisée pour la boule
    private float MAX_SPEED = 20.0f;
    // Couleur de la boule
    private int color = Color.GREEN;
    // Le rectangle qui correspond à la position de départ de la boule
    private RectF initialRectangle = null;
    // Le rectangle de collision
    private RectF rectangle = null;
    // Vitesse sur l'axe x
    private float speedX = 0;
    // Vitesse sur l'axe y
    private float speedY = 0;
    // Taille de l'écran en hauteur
    private int height = -1;
    // Taille de l'écran en largeur
    private int width = -1;
    public Ball(float size) {
        RADIUS = size/2;
        rectangle = new RectF();
    }

    public float getSpeedX() {
        return speedX;
    }

    public float getSpeedY() {
        return speedY;
    }

    public int getColor() {
        return color;
    }

    // A partir du rectangle initial on détermine la position de la boule
    public void setInitialRectangle(RectF pInitialRectangle) {
        this.initialRectangle = pInitialRectangle;
        rectangle.set(pInitialRectangle.centerX() - RADIUS, pInitialRectangle.centerY() - RADIUS,
                pInitialRectangle.centerX() + RADIUS, pInitialRectangle.centerY() + RADIUS
        );
    }

    public float getX() {
        return rectangle.centerX();
    }

    public void setX(float x) {
        rectangle.set(x - RADIUS, rectangle.top, x + RADIUS, rectangle.bottom);
    }

    public float getY() {
        return rectangle.centerY();
    }

    public void setY(float y) {
        rectangle.set(rectangle.left, y - RADIUS, rectangle.right, y + RADIUS);
    }

    // Utilisé quand on rebondit sur les murs horizontaux
    public void changeXSpeed() {
        speedX = -speedX / 2;
    }

    // Utilisé quand on rebondit sur les murs verticaux
    public void changeYSpeed() {
        speedY = -speedY / 2;
    }

    public void setHeight(int pHeight) {
        this.height = pHeight;
    }

    public void setWidth(int pWidth) {
        this.width = pWidth;
    }

    // Mettre à jour les coordonnées de la boule
    public RectF putXAndY(float pX, float pY) {
        float x = rectangle.centerX();
        float y = rectangle.centerY();
        speedX += pX / COMPENSATEUR;
        if(speedX > MAX_SPEED)
            speedX = MAX_SPEED;
        if(speedX < -MAX_SPEED)
            speedX = -MAX_SPEED;

        speedY += pY / COMPENSATEUR;
        if(speedY > MAX_SPEED)
            speedY = MAX_SPEED;
        if(speedY < -MAX_SPEED)
            speedY = -MAX_SPEED;

        x += speedX;
        y += speedY;

        rectangle.set(x - RADIUS, y - RADIUS, x + RADIUS, y + RADIUS);

        return rectangle;
    }

    // Remet la boule à sa position de départ
    public void reset() {
        speedX = 0;
        speedX = 0;
        rectangle.set(initialRectangle.centerX() - RADIUS, initialRectangle.centerY() - RADIUS,
                initialRectangle.centerX() + RADIUS, initialRectangle.centerY() + RADIUS
        );
    }

    public RectF getRectangle() {
        return rectangle;
    }

    public void setMaximumSpeed(int maximumSpeed) {
        this.MAX_SPEED = maximumSpeed;
    }
}
