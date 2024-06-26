package frc.robot.subsystems;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.DriveConstants;
import frc.robot.Constants.IntakeConstants;
import frc.robot.Constants.LimelightConstants;
import frc.robot.Constants.OIConstants;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.LimelightSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.PivotSubsystem;
// import frc.robot.subsystems.LauncherPivotSubsystem;
import frc.robot.subsystems.LauncherSubsystem;
import frc.robot.subsystems.LauncherPivotSubsystem;
import frc.robot.RobotContainer;

public class AutoShoot extends SubsystemBase {

    public LimelightSubsystem Limelight;
    public DriveSubsystem Drive;
    public IntakeSubsystem Intake;
    public PivotSubsystem Pivot;
    // public RobotPivotsSubsystem RobotPivot;
    public LauncherSubsystem Launcher;
    public LauncherPivotSubsystem LauncherPivot;

    public double crosshairAdjust = 0;


    public AutoShoot(LimelightSubsystem Limelight, DriveSubsystem Drive, IntakeSubsystem Intake, PivotSubsystem Pivot,
            LauncherSubsystem Launcher, LauncherPivotSubsystem LauncherPivot) {
        this.Limelight = Limelight;
        this.Drive = Drive;
        this.Intake = Intake;
        this.Pivot = Pivot;
        // this.RobotPivot = RobotPivot;
        this.Launcher = Launcher;
        this.LauncherPivot = LauncherPivot;
    }

    public Command SpeakerAlign() {
        return run(() -> {
            NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(1);

            // check against 'tv' before aligning
            double tv = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0);
            double tx = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0);
            double ty = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ty").getDouble(0);
            NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(3);
            // double[] poseArray = NetworkTableInstance.getDefault().getTable("limelight")
            // .getEntry("targetpose_cameraspace").getDoubleArray(new double[6]);
            // double angleError = poseArray[5];
            double ySpeed = 0.0;
            double xSpeed = 0.0;
            double rotSpeed = 0.0;
            double yAngle;

            Limelight.SpeakerAlignServoPos();

            // double yawCalculated = (Math.signum(Drive.m_gyro.getYaw()) * Math.PI) -
            // (Math.toRadians(Drive.m_gyro.getYaw()));

            // double kpTurn = 0.25;
            float KpStrafe = 0.01f;

            // if (Math.abs(Math.toDegrees(yawCalculated)) > 1 || Math.abs(xError) > .25){
            // rot = MathUtil.clamp((kpTurn * yawCalculated) + .05, -0.25, 0.25);

            rotSpeed = (KpStrafe * (tx));
            // double yAngle = ((Math.pow(0.0895 * ty, 2)) - (3.8796 * ty) + 6.3136);
            if (((ty + crosshairAdjust) >= -3.0) && ((ty + crosshairAdjust) < 6.0)) {
                yAngle = ((0.0198 * (Math.pow(ty + crosshairAdjust, 2))) - (2.6035 * (ty + crosshairAdjust)) - 5.2846);
            } else {
                yAngle = ((-0.211 * (Math.pow(ty + crosshairAdjust, 2))) + (2.47 * (ty + crosshairAdjust)) - 27.4);
            }

            double intakeAngle = (0.15 * (yAngle)) + 10;

            // ySpeed = -(KpStrafe * (tx + 4.7)); //tx = horizontal error, strafe direction
            // in robot coordinates
            // xSpeed = (KpStrafe * (16 - ty));

            ySpeed = 0;
            xSpeed = 0;
            // }

            if (rotSpeed > 0.50) {
                rotSpeed = 0.50;
            } else if (rotSpeed < -0.50) {
                rotSpeed = -0.50;
            }

            if (tv < 0.9999) {
                rotSpeed = -MathUtil.applyDeadband(
                        Math.pow(RobotContainer.m_driverController.getRightX(), 2)
                                * Math.signum(RobotContainer.m_driverController.getRightX()) * 0.25,
                        OIConstants.kDriveDeadband);
            }

            if (yAngle > 0.0) {
                yAngle = 0.0;
            }

            if (yAngle < -30) {
                yAngle = -30;
            }

            if (intakeAngle < 2.5) {
                intakeAngle = 2.5;
            }

            if (intakeAngle > 10.0) {
                intakeAngle = 10.0;
            }

            Drive.drive(
                    -MathUtil.applyDeadband(
                            Math.pow(RobotContainer.m_driverController.getLeftY(), 2)
                                    * Math.signum(RobotContainer.m_driverController.getLeftY()) * 0.25,
                            OIConstants.kDriveDeadband),
                    -MathUtil.applyDeadband(
                            Math.pow(RobotContainer.m_driverController.getLeftX(), 2)
                                    * Math.signum(RobotContainer.m_driverController.getLeftX()) * 0.25,
                            OIConstants.kDriveDeadband),
                    rotSpeed,
                    // Rate limit = true sets speed to 0. Why? This is something to fix.
                    true, false);
            // if ( (tv > 0.9999) && (Math.abs(rotSpeed) < 0.025) && (Math.abs(xSpeed) <
            // 0.025) && (Math.abs(ySpeed) < 0.025)){
            // // Commands.runOnce(() -> {

            LauncherPivot.setLauncherPivotGoal(yAngle);

            Pivot.intakePos(intakeAngle);

            // Launcher.shoot();

            // // }, Intake);
            // }

            // debug test
            // System.out.printf("Rot, X, Y Speeds");
            // System.out.printf("%f\n", rotSpeed);
            // System.out.printf("%f\n", xSpeed);
            // System.out.printf("%f\n", ySpeed);

            System.out.printf("%f\n", yAngle);
            System.out.printf("%f\n", intakeAngle);
            
        });

    }

    public Command stopLED(){
        return runOnce(() -> {
            NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
            table.getEntry("ledMode").setNumber(1);
        });
    }




    public Command increaseCrosshairAdjust(){
        return runOnce(() -> {
            crosshairAdjust += 0.5;  //<================ tune this increment
        });
    }

    public Command decreaseCrosshairAdjust(){
        return runOnce(() -> {
            crosshairAdjust -= 0.5;  //<================ tune this increment
        });
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Speaker Align Crosshair Adjust", crosshairAdjust);
    }

    

}