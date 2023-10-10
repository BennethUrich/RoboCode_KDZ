package secondkilldozer;

import robocode.*;

public class secondkilldozer extends AdvancedRobot {
    
    @Override
    public void run() {
        setAdjustRadarForRobotTurn(true);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        while (true) {
            turnRadarRightRadians(Double.POSITIVE_INFINITY);
        }
    }
    
    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        double bulletPower = event.getEnergy() > 16 ? 3 : Math.min(getEnergy() / 4, event.getEnergy() + 0.1);
        double absoluteBearingRadians = getHeadingRadians() + event.getBearingRadians();
        double bearingFromGun = robocode.util.Utils.normalRelativeAngle(absoluteBearingRadians - getGunHeadingRadians());
        double bearingFromRadar = robocode.util.Utils.normalRelativeAngle(absoluteBearingRadians - getRadarHeadingRadians());
        
        // Gira o radar na direção do robô inimigo
        setTurnRadarRightRadians(bearingFromRadar * 2);
        
        // Gira o canhão na direção do robô inimigo
        setTurnGunRightRadians(bearingFromGun);
        
        // Verifica se o canhão está apontando para o robô inimigo e o inimigo está a uma distância segura
        if (Math.abs(bearingFromGun) < 0.03 && getGunHeat() == 0 && event.getDistance() < 300) {
            setFire(bulletPower);
        }
        
        // Verifica se o robô está muito próximo do inimigo e move-se para trás
        if (event.getDistance() < 100) {
            setBack(50);
        } else {
            setAhead(100);
        }
    }
    
    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        double bearing = event.getBearingRadians();
        turnRightRadians(robocode.util.Utils.normalRelativeAngle(bearing + Math.PI/2));
    }
    
    @Override
    public void onHitWall(HitWallEvent event) {
        double bearing = event.getBearingRadians();
        turnRightRadians(robocode.util.Utils.normalRelativeAngle(bearing + Math.PI/2));
    }
}
