package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TranslationalVelConstraint;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.movement.MecanumDrive;

import org.firstinspires.ftc.teamcode.rrLibs.tuning.TuningOpModes;

@Autonomous(name = "Drive Test", group = "Auto Testing")
public final class DriveTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d beginPose = new Pose2d(0, 0, 0);
        if (TuningOpModes.DRIVE_CLASS.equals(MecanumDrive.class)) {
            MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);

            waitForStart();

            Actions.runBlocking(
                    drive.actionBuilder(beginPose)
                            .splineToLinearHeading(new Pose2d(15, 15,-Math.PI/2),Math.PI/2,new TranslationalVelConstraint(5.0))
                            //.strafeToConstantHeading(new Vector2d(0, 0), new TranslationalVelConstraint(5.0))
                            .build());
            drive.writePos();

//            String fileName = "x.json";
//            File file = AppUtil.getInstance().getSettingsFile(fileName);
//            ReadWriteFile.writeFile(file, drive.pose.position.x+"");
////            ReadWriteFile.writeFile(file, drive.pose.position.y+"");
////            ReadWriteFile.writeFile(file, drive.pose.position.x+"");
        }else {
            throw new RuntimeException();
        }
    }
}
