package frc.robot.subsystems;

import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkPIDController;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ShooterConstants;

public class LauncherSubsystem extends SubsystemBase {
    private final CANSparkMax leftShooter, rightShooter;
    private final SparkPIDController leftShoooterPIDController;
    private final SparkPIDController rightShoooterPIDController;

    public LauncherSubsystem() {
        leftShooter = new CANSparkMax(ShooterConstants.kleftShooterCanId, MotorType.kBrushless);
        leftShooter.restoreFactoryDefaults();
        leftShooter.setIdleMode(ShooterConstants.kShootingMotorIdleMode);
        leftShooter.setSmartCurrentLimit(ShooterConstants.kShootingMotorCurrentLimit);

        leftShoooterPIDController = leftShooter.getPIDController();
        leftShoooterPIDController.setP(ShooterConstants.shooterKp);

        leftShooter.burnFlash();

        rightShooter = new CANSparkMax(ShooterConstants.kRightShooterCanId, MotorType.kBrushless);
        rightShooter.setIdleMode(ShooterConstants.kShootingMotorIdleMode);
        rightShooter.setSmartCurrentLimit(ShooterConstants.kShootingMotorCurrentLimit);

        rightShoooterPIDController = rightShooter.getPIDController();
        rightShoooterPIDController.setP(ShooterConstants.shooterKp);

        rightShooter.burnFlash();
    }

    public Command shoot() {
        return runOnce(() -> {
            leftShooter.set(ShooterConstants.leftShootSpeed);
            rightShooter.set(ShooterConstants.rightShootSpeed);
        });
    }

    public Command autoShooterSpeedAdjust (){
        return runOnce(() -> {
            leftShooter.set(ShooterConstants.leftShootSpeed * 0.8);
            rightShooter.set(ShooterConstants.rightShootSpeed * 0.8);
        });
    }

    public Command stop() {
        return runOnce(() -> {
            leftShooter.set(0.0);
            rightShooter.set(0.0);
        });
    }
}
