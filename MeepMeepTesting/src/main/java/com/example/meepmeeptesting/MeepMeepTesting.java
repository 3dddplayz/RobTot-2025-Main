package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.MecanumKinematics;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Rotation2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
//import com.acmerobotics.roadrunner.ftc.Actions;

public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(700);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(40, 40, Math.toRadians(180), Math.toRadians(180), 15)
                .setDimensions(15,18)
                .build();

        myBot.runAction(
            new SequentialAction(
                new SleepAction(0),

                myBot.getDrive().actionBuilder(new Pose2d(24, -60, Math.PI/2))
                        //first place
                        .strafeTo(new Vector2d(8,-24-10))
                        .strafeTo(new Vector2d(8,-24-8))


                        //push 1 red to HP area
//                        .strafeTo(new Vector2d(8,-24-15))
//                        .strafeTo(new Vector2d(32,-24-15))
//                        .strafeTo(new Vector2d(32,-24-15))
//                        .setTangent(new Rotation2d(0,1))
//                        .splineToLinearHeading(new Pose2d(40,-8,3*Math.PI/2),Math.PI/2)
//
//                        .strafeToLinearHeading(new Vector2d(47,-8),-Math.PI/2)
//                        .strafeTo(new Vector2d(47,-24*3+11))
//                        .strafeToLinearHeading(new Vector2d(8,-24-10),Math.PI/2)
//                        .strafeTo(new Vector2d(8,-24-8))

                        //regrab 1

                        .setTangent(-Math.PI/3)
                        .splineToLinearHeading(new Pose2d(47,-24*3+15,-Math.PI/2),-Math.PI/4)
                        .strafeTo(new Vector2d(47,-24*3+11))

                        //regrab 2
                        .strafeToLinearHeading(new Vector2d(8,-24-10),Math.PI/2)
                        .strafeTo(new Vector2d(8,-24-8))
                        .setTangent(-Math.PI/3)
                        .splineToLinearHeading(new Pose2d(47,-24*3+15,-Math.PI/2),-Math.PI/4)
                        .strafeTo(new Vector2d(47,-24*3+11))

                        //regrab 3
                        .strafeToLinearHeading(new Vector2d(8,-24-10),Math.PI/2)
                        .strafeTo(new Vector2d(8,-24-8))
                        .setTangent(-Math.PI/3)
                        .splineToLinearHeading(new Pose2d(47,-24*3+15,-Math.PI/2),-Math.PI/4)
                        .strafeTo(new Vector2d(47,-24*3+11))

                        //regrab 4
                        .strafeToLinearHeading(new Vector2d(8,-24-10),Math.PI/2)
                        .strafeTo(new Vector2d(8,-24-8))
                        .setTangent(-Math.PI/3)
                        .splineToLinearHeading(new Pose2d(47,-24*3+15,-Math.PI/2),-Math.PI/4)
                        .strafeTo(new Vector2d(47,-24*3+11))


                .build()));

        meepMeep.setBackground(MeepMeep.Background.FIELD_INTO_THE_DEEP_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}