package KillDozer;

import robocode.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KillDozer extends AdvancedRobot {

    private List<Bullet> bullets;

    public void run() {
        setColors(Color.yellow, Color.gray, Color.black);
        setAdjustRadarForRobotTurn(true);
        setAdjustGunForRobotTurn(true);

        bullets = new ArrayList<>();

        while (true) {
            turnRadarRight(Double.POSITIVE_INFINITY);
            updateBullets();
        }
    }

    public void onScannedRobot(ScannedRobotEvent event) {
        double enemyBearing = event.getBearingRadians();
        double enemyDistance = event.getDistance();

        // Gira o radar para o inimigo
        double radarTurn = getHeadingRadians() + enemyBearing - getRadarHeadingRadians();
        setTurnRadarRightRadians(radarTurn * 2);

        // Gira o canhão para o inimigo
        double gunTurn = getHeadingRadians() + enemyBearing - getGunHeadingRadians();
        setTurnGunRightRadians(gunTurn);

        // Atira no inimigo com a máxima potência
        setFire(3);

        // Move-se para evitar ser atingido
        dodgeBullets();
    }

    public void onHitByBullet(HitByBulletEvent event) {
        // Move-se em uma direção perpendicular à trajetória da bala
        double bulletHeading = event.getBullet().getHeadingRadians();
        double moveAngle = bulletHeading - Math.PI / 2;
        setTurnRightRadians(moveAngle - getHeadingRadians());

        // Move-se para trás para evitar a bala
        setBack(100);
    }

    private void dodgeBullets() {
        // Verifica se há balas próximas e as esquiva
        Bullet bullet = getClosestBulletToRobot();

        if (bullet != null) {
            // Calcula o ângulo da bala em relação ao robô
            double bulletHeading = bullet.getHeadingRadians();
            double moveAngle = bulletHeading - Math.PI / 2;

            // Ajusta o ângulo de movimento de acordo com a direção do robô
            if (bullet.getVelocity() < 0) {
                moveAngle += Math.PI;
            }

            // Move-se na direção perpendicular à bala
            setTurnRightRadians(moveAngle - getHeadingRadians());
            setAhead(100);
        }
    }

    private Bullet getClosestBulletToRobot() {
        // Encontra a bala mais próxima em voo
        Bullet closestBullet = null;
        double minDistance = Double.MAX_VALUE;

        for (Bullet bullet : bullets) {
            if (bullet.isActive()) {
               double bulletX = bullet.getX();
			   double bulletY = bullet.getY();
			   double robotX = getX();
			   double robotY = getY();
			    double distance = Math.sqrt(Math.pow(bulletX - robotX, 2) + Math.pow(bulletY - robotY, 2));
                if (distance < minDistance) {
                    closestBullet = bullet;
                    minDistance = distance;
                }
            }
        }

        return closestBullet;
    }

    public void onBulletHit(BulletHitEvent event) {
        // Remove a bala da lista quando atinge o oponente
        Bullet bullet = event.getBullet();
        bullets.remove(bullet);
    }

    public void onBulletMissed(BulletMissedEvent event) {
        // Remove a bala da lista quando erra o tiro
        Bullet bullet = event.getBullet();
        bullets.remove(bullet);
    }

    public void onBulletHitBullet(BulletHitBulletEvent event) {
        // Remove a bala da lista quando colide com outra bala
        Bullet bullet = event.getBullet();
        bullets.remove(bullet);
    }

    public void onBulletFired(Bullet bullet) {
        // Adiciona a bala à lista quando é disparada
        bullets.add(bullet);
    }

    private void updateBullets() {
        // Remove as balas inativas da lista
        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            if (!bullet.isActive()) {
                iterator.remove();
            }
        }
    }
}
