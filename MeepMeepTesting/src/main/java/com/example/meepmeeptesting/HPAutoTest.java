package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Rotation2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;
//import com.acmerobotics.roadrunner.ftc.Actions;

public class HPAutoTest {
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
                        .setTangent(3*Math.PI/4)
                        .splineToConstantHeading(new Vector2d(11,-24-10),3*Math.PI/4)
                        .splineToConstantHeading(new Vector2d(8,-24-8),Math.PI)


                        //push 1 red to HP area


                        //regrab 1

                        .setTangent(0)
                        .splineToSplineHeading(new Pose2d(43,-24*3+15,3*Math.PI/2),0)
                        .splineToConstantHeading(new Vector2d(47,-24*3+11),-Math.PI/2)

                        //regrab 2
                        .setTangent(Math.PI)
                        .splineToSplineHeading(new Pose2d(11,-24-10,-3*Math.PI/2),Math.PI)
                        .splineToConstantHeading(new Vector2d(8,-24-8),Math.PI)
                        //place
                        .setTangent(0)
                        .splineToSplineHeading(new Pose2d(43,-24*3+15,3*Math.PI/2),0)
                        .splineToConstantHeading(new Vector2d(47,-24*3+11),-Math.PI/2)

                        //regrab 3
                        .setTangent(Math.PI)
                        .splineToSplineHeading(new Pose2d(11,-24-10,-3*Math.PI/2),Math.PI)
                        .splineToConstantHeading(new Vector2d(8,-24-8),Math.PI)
                        //place
                        .setTangent(0)
                        .splineToSplineHeading(new Pose2d(43,-24*3+15,3*Math.PI/2),0)
                        .splineToConstantHeading(new Vector2d(47,-24*3+11),-Math.PI/2)


//


                .build()));

        meepMeep.setBackground(MeepMeep.Background.FIELD_INTO_THE_DEEP_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}