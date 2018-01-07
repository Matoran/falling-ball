package run.mato.hepia.labo4.Model;

import android.graphics.Color;
import android.graphics.RectF;

/**
 * Created by matoran on 1/6/18.
 */

public class Ball {
    // Rayon de la boule
    public static float RADIUS;
    // Vitesse maximale autorisée pour la boule
    private static final float MAX_SPEED = 20.0f;
    // Permet à la boule d'accélérer moins vite
    private static final float COMPENSATEUR = 8.0f;
    // Utilisé pour compenser les rebonds
    private static final float REBOND = 1.75f;
    // Couleur de la boule
    private int color = Color.GREEN;
    // Le rectangle qui correspond à la position de départ de la boule
    private RectF initialRectangle = null;
    // Le rectangle de collision
    private RectF rectangle = null;
    // Coordonnées en x
    private float x;
    // Coordonnées en y
    private float y;
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

    public int getColor() {
        return color;
    }

    // A partir du rectangle initial on détermine la position de la boule
    public void setInitialRectangle(RectF pInitialRectangle) {
        this.initialRectangle = pInitialRectangle;
        this.x = pInitialRectangle.left + RADIUS;
        this.y = pInitialRectangle.top + RADIUS;
    }

    public float getX() {
        return x;
    }

    public void setPosX(float pPosX) {
        x = pPosX;

        // Si la boule sort du cadre, on rebondit
        if(x < RADIUS) {
            x = RADIUS;
            // Rebondir, c'est changer la direction de la balle
            speedY = -speedY / REBOND;
        } else if(x > width - RADIUS) {
            x = width - RADIUS;
            speedY = -speedY / REBOND;
        }
    }

    public float getY() {
        return y;
    }

    public void setPosY(float pPosY) {
        y = pPosY;
        if(y < RADIUS) {
            y = RADIUS;
            speedX = -speedX / REBOND;
        } else if(y > height - RADIUS) {
            y = height - RADIUS;
            speedX = -speedX / REBOND;
        }
    }

    // Utilisé quand on rebondit sur les murs horizontaux
    public void changeXSpeed() {
        speedX = -speedX;
    }

    // Utilisé quand on rebondit sur les murs verticaux
    public void changeYSpeed() {
        speedY = -speedY;
    }

    public void setHeight(int pHeight) {
        this.height = pHeight;
    }

    public void setWidth(int pWidth) {
        this.width = pWidth;
    }

    // Mettre à jour les coordonnées de la boule
    public RectF putXAndY(float pX, float pY) {
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

        setPosX(x + speedY);
        setPosY(y + speedX);

        // Met à jour les coordonnées du rectangle de collision
        rectangle.set(x - RADIUS, y - RADIUS, x + RADIUS, y + RADIUS);

        return rectangle;
    }

    // Remet la boule à sa position de départ
    public void reset() {
        speedX = 0;
        speedY = 0;
        this.x = initialRectangle.left + RADIUS;
        this.y = initialRectangle.top + RADIUS;
    }
}
