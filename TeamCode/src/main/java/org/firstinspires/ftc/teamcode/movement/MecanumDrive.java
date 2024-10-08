package org.firstinspires.ftc.teamcode.movement;

import static android.os.SystemClock.sleep;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.canvas.Canvas;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.*;
import com.acmerobotics.roadrunner.AngularVelConstraint;
import com.acmerobotics.roadrunner.DualNum;
import com.acmerobotics.roadrunner.HolonomicController;
import com.acmerobotics.roadrunner.MecanumKinematics;
import com.acmerobotics.roadrunner.MinVelConstraint;
import com.acmerobotics.roadrunner.MotorFeedforward;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Pose2dDual;
import com.acmerobotics.roadrunner.ProfileAccelConstraint;
import com.acmerobotics.roadrunner.Time;
import com.acmerobotics.roadrunner.TimeTrajectory;
import com.acmerobotics.roadrunner.TimeTurn;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.TurnConstraints;
import com.acmerobotics.roadrunner.Twist2dDual;
import com.acmerobotics.roadrunner.VelConstraint;
import com.acmerobotics.roadrunner.ftc.DownsampledWriter;
import com.acmerobotics.roadrunner.ftc.Encoder;
import com.acmerobotics.roadrunner.ftc.FlightRecorder;
import com.acmerobotics.roadrunner.ftc.LazyImu;
import com.acmerobotics.roadrunner.ftc.LynxFirmware;
import com.acmerobotics.roadrunner.ftc.OverflowEncoder;
import com.acmerobotics.roadrunner.ftc.PositionVelocityPair;
import com.acmerobotics.roadrunner.ftc.RawEncoder;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.ReadWriteFile;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.teamcode.rrLibs.messages.Drawing;
import org.firstinspires.ftc.teamcode.rrLibs.messages.DriveCommandMessage;
import org.firstinspires.ftc.teamcode.rrLibs.messages.MecanumCommandMessage;
import org.firstinspires.ftc.teamcode.rrLibs.messages.MecanumLocalizerInputsMessage;
import org.firstinspires.ftc.teamcode.rrLibs.messages.PoseMessage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.OpenCvWebcam;

import java.io.File;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Config
public final class MecanumDrive {
    public static class Params {
        // IMU orientation
        // TODO: fill in these values based on
        //   see https://ftc-docs.firstinspires.org/en/latest/programming_resources/imu/imu.html?highlight=imu#physical-hub-mounting
        public RevHubOrientationOnRobot.LogoFacingDirection logoFacingDirection =
                RevHubOrientationOnRobot.LogoFacingDirection.UP;
        public RevHubOrientationOnRobot.UsbFacingDirection usbFacingDirection =
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD;

        // drive model parameters
        //2893
        //8.5-(2/16)
        //mm/tick = (48*Math.PI)/(2000)
        //in/mm = 25.4
        public double inPerTick = (48 * Math.PI) / (2000 * 25.4);//.0028949
        public double lateralInPerTick = inPerTick;
        public double trackWidthTicks = 3169.3267881669526;

        // feedforward parameters (in tick units)
        public double kS = 1.0755508833750609;
        public double kV = 0.0006032725545455405;
        public double kA = 0.0001;

        // path profile parameters (in inches)
        public double maxWheelVel = 50;
        public double minProfileAccel = -30;
        public double maxProfileAccel = 50;

        // turn profile parameters (in radians)
        public double maxAngVel = Math.PI; // shared with path
        public double maxAngAccel = Math.PI;

        // path controller gains
        public double axialGain = 12;
        public double lateralGain = 12;
        public double headingGain = 12; // shared with turn

        public double axialVelGain = 1;
        public double lateralVelGain = 1;
        public double headingVelGain = .5; // shared with turn
    }

    public static Params PARAMS = new Params();

    public final MecanumKinematics kinematics = new MecanumKinematics(
            PARAMS.inPerTick * PARAMS.trackWidthTicks, PARAMS.inPerTick / PARAMS.lateralInPerTick);

    public final TurnConstraints defaultTurnConstraints = new TurnConstraints(
            PARAMS.maxAngVel, -PARAMS.maxAngAccel, PARAMS.maxAngAccel);
    public final VelConstraint defaultVelConstraint =
            new MinVelConstraint(Arrays.asList(
                    kinematics.new WheelVelConstraint(PARAMS.maxWheelVel),
                    new AngularVelConstraint(PARAMS.maxAngVel)
            ));
    public final AccelConstraint defaultAccelConstraint =
            new ProfileAccelConstraint(PARAMS.minProfileAccel, PARAMS.maxProfileAccel);

    public final DcMotorEx leftFront, leftBack, rightBack, rightFront;

    public final Servo extendo;

    public final VoltageSensor voltageSensor;

    public final LazyImu lazyImu;

    public final Localizer localizer;
    public Pose2d pose;

    private final LinkedList<Pose2d> poseHistory = new LinkedList<>();

    private final DownsampledWriter estimatedPoseWriter = new DownsampledWriter("ESTIMATED_POSE", 50_000_000);
    private final DownsampledWriter targetPoseWriter = new DownsampledWriter("TARGET_POSE", 50_000_000);
    private final DownsampledWriter driveCommandWriter = new DownsampledWriter("DRIVE_COMMAND", 50_000_000);
    private final DownsampledWriter mecanumCommandWriter = new DownsampledWriter("MECANUM_COMMAND", 50_000_000);

    public double targetHeading = 0;

    public class DriveLocalizer implements Localizer {
        public final Encoder leftFront, leftBack, rightBack, rightFront;
        public final IMU imu;

        private int lastLeftFrontPos, lastLeftBackPos, lastRightBackPos, lastRightFrontPos;
        private Rotation2d lastHeading;
        private boolean initialized;

        public DriveLocalizer() {
            leftFront = new OverflowEncoder(new RawEncoder(MecanumDrive.this.leftFront));
            leftBack = new OverflowEncoder(new RawEncoder(MecanumDrive.this.leftBack));
            rightBack = new OverflowEncoder(new RawEncoder(MecanumDrive.this.rightBack));
            rightFront = new OverflowEncoder(new RawEncoder(MecanumDrive.this.rightFront));

            imu = lazyImu.get();

            // TODO: reverse encoders if needed
            //rightFront.setDirection(DcMotorSimple.Direction.REVERSE);
//               leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        }

        @Override
        public Twist2dDual<Time> update() {
            PositionVelocityPair leftFrontPosVel = leftFront.getPositionAndVelocity();
            PositionVelocityPair leftBackPosVel = leftBack.getPositionAndVelocity();
            PositionVelocityPair rightBackPosVel = rightBack.getPositionAndVelocity();
            PositionVelocityPair rightFrontPosVel = rightFront.getPositionAndVelocity();

            YawPitchRollAngles angles = imu.getRobotYawPitchRollAngles();

            FlightRecorder.write("MECANUM_LOCALIZER_INPUTS", new MecanumLocalizerInputsMessage(
                    leftFrontPosVel, leftBackPosVel, rightBackPosVel, rightFrontPosVel, angles));

            Rotation2d heading = Rotation2d.exp(angles.getYaw(AngleUnit.RADIANS));

            if (!initialized) {
                initialized = true;

                lastLeftFrontPos = leftFrontPosVel.position;
                lastLeftBackPos = leftBackPosVel.position;
                lastRightBackPos = rightBackPosVel.position;
                lastRightFrontPos = rightFrontPosVel.position;

                lastHeading = heading;

                return new Twist2dDual<>(
                        Vector2dDual.constant(new Vector2d(0.0, 0.0), 2),
                        DualNum.constant(0.0, 2)
                );
            }

            double headingDelta = heading.minus(lastHeading);
            Twist2dDual<Time> twist = kinematics.forward(new MecanumKinematics.WheelIncrements<>(
                    new DualNum<Time>(new double[]{
                            (leftFrontPosVel.position - lastLeftFrontPos),
                            leftFrontPosVel.velocity,
                    }).times(PARAMS.inPerTick),
                    new DualNum<Time>(new double[]{
                            (leftBackPosVel.position - lastLeftBackPos),
                            leftBackPosVel.velocity,
                    }).times(PARAMS.inPerTick),
                    new DualNum<Time>(new double[]{
                            (rightBackPosVel.position - lastRightBackPos),
                            rightBackPosVel.velocity,
                    }).times(PARAMS.inPerTick),
                    new DualNum<Time>(new double[]{
                            (rightFrontPosVel.position - lastRightFrontPos),
                            rightFrontPosVel.velocity,
                    }).times(PARAMS.inPerTick)
            ));

            lastLeftFrontPos = leftFrontPosVel.position;
            lastLeftBackPos = leftBackPosVel.position;
            lastRightBackPos = rightBackPosVel.position;
            lastRightFrontPos = rightFrontPosVel.position;

            lastHeading = heading;

            return new Twist2dDual<>(
                    twist.line,
                    DualNum.cons(headingDelta, twist.angle.drop(1))
            );
        }
    }

    OpenCvWebcam cam;
    public CameraDetectPipeline pipeline;
    public String team;
    public MecanumDrive(HardwareMap hardwareMap, Pose2d pose) {

        this.pose = pose;

        targetHeading = pose.heading.toDouble();

        LynxFirmware.throwIfModulesAreOutdated(hardwareMap);

        for (LynxModule module : hardwareMap.getAll(LynxModule.class)) {
            module.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }

        // TODO: make sure your config has motors with these names (or change them)
        //   see https://ftc-docs.firstinspires.org/en/latest/hardware_and_software_configuration/configuring/index.html
        leftFront = hardwareMap.get(DcMotorEx.class, "leftFo3");
        leftBack = hardwareMap.get(DcMotorEx.class, "leftB");
        rightBack = hardwareMap.get(DcMotorEx.class, "rightBo2");
        rightFront = hardwareMap.get(DcMotorEx.class, "rightFo1");

        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // TODO: reverse motor directions if needed
        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);

        extendo = hardwareMap.get(Servo.class, "extendo");

        // TODO: make sure your config has an IMU with this name (can be BNO or BHI)
        //   see https://ftc-docs.firstinspires.org/en/latest/hardware_and_software_configuration/configuring/index.html
        lazyImu = new LazyImu(hardwareMap, "imu", new RevHubOrientationOnRobot(
                PARAMS.logoFacingDirection, PARAMS.usbFacingDirection));

        voltageSensor = hardwareMap.voltageSensor.iterator().next();

        //localizer = new ThreeDeadWheelLocalizer(hardwareMap, PARAMS.inPerTick);
        localizer = new TwoDeadWheelLocalizer(hardwareMap, lazyImu.get(), PARAMS.inPerTick);

        FlightRecorder.write("MECANUM_PARAMS", PARAMS);

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        WebcamName webcamName = hardwareMap.get(WebcamName.class, "webcam");
        cam = OpenCvCameraFactory.getInstance().createWebcam(webcamName, cameraMonitorViewId);

        pipeline = new CameraDetectPipeline();

        // Open async and start streaming inside opened callback
        cam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                cam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);


                cam.setPipeline(pipeline);

            }

            @Override
            public void onError(int errorCode) {
            }
        });
    }
    public double angleCor(double ang){
        if(Math.abs(ang)>Math.abs(ang+Math.PI*2)){
            ang = ang+Math.PI*2;
        }else if(Math.abs(ang)>Math.abs(ang-Math.PI*2)){
            ang = ang-Math.PI*2;
        }
        return ang;
    }
    ElapsedTime time = new ElapsedTime();
    public void TeleOpMove(PoseVelocity2d targetVel) {
        if(Math.abs(targetVel.angVel)>0.05){
            time.reset();
        }
        if(Math.abs(targetVel.angVel)<0.05 && time.time()>1){
            double headingChange = angleCor(1*(targetHeading - pose.heading.toDouble()));
            targetVel = new PoseVelocity2d(new Vector2d(targetVel.linearVel.x,targetVel.linearVel.y),targetVel.angVel+headingChange);
        }else targetHeading = pose.heading.toDouble();

        setDrivePowers(targetVel);
    }


    public void setDrivePowers(PoseVelocity2d powers) {
        MecanumKinematics.WheelVelocities<Time> wheelVels = new MecanumKinematics(1).inverse(
                PoseVelocity2dDual.constant(powers, 1));

        double maxPowerMag = 1;
        for (DualNum<Time> power : wheelVels.all()) {
            maxPowerMag = Math.max(maxPowerMag, power.value());
        }
        TelemetryPacket packet = new TelemetryPacket();
        FtcDashboard dash = FtcDashboard.getInstance();
        packet.put("wheel power",(wheelVels.leftFront.get(0) / maxPowerMag));
        dash.sendTelemetryPacket(packet);
        leftFront.setPower(wheelVels.leftFront.get(0) / maxPowerMag);
        leftBack.setPower(wheelVels.leftBack.get(0) / maxPowerMag);
        rightBack.setPower(wheelVels.rightBack.get(0) / maxPowerMag);
        rightFront.setPower(wheelVels.rightFront.get(0) / maxPowerMag);
    }

    public final class FollowTrajectoryAction implements Action {
        public final TimeTrajectory timeTrajectory;
        private double beginTs = -1;

        private final double[] xPoints, yPoints;

        public FollowTrajectoryAction(TimeTrajectory t) {
            timeTrajectory = t;

            List<Double> disps = com.acmerobotics.roadrunner.Math.range(
                    0, t.path.length(),
                    Math.max(2, (int) Math.ceil(t.path.length() / 2)));
            xPoints = new double[disps.size()];
            yPoints = new double[disps.size()];
            for (int i = 0; i < disps.size(); i++) {
                Pose2d p = t.path.get(disps.get(i), 1).value();
                xPoints[i] = p.position.x;
                yPoints[i] = p.position.y;
            }
        }

        @Override
        public boolean run(@NonNull TelemetryPacket p) {
            double t;
            if (beginTs < 0) {
                beginTs = Actions.now();
                t = 0;
            } else {
                t = Actions.now() - beginTs;
            }
            Pose2dDual<Time> txWorldTarget = timeTrajectory.get(t);
            targetPoseWriter.write(new PoseMessage(txWorldTarget.value()));
            PoseVelocity2d robotVelRobot = updatePoseEstimate();
            Pose2d error = txWorldTarget.value().minusExp(pose);
            if ((t >= timeTrajectory.duration && error.position.norm() < 0.25  //extra correction
                    && robotVelRobot.linearVel.norm() < 0.5)
                    || t >= timeTrajectory.duration + 2) {
                leftFront.setPower(0);
                leftBack.setPower(0);
                rightBack.setPower(0);
                rightFront.setPower(0);

                return false;
            }




            PoseVelocity2dDual<Time> command = new HolonomicController(
                    PARAMS.axialGain, PARAMS.lateralGain, PARAMS.headingGain,
                    PARAMS.axialVelGain, PARAMS.lateralVelGain, PARAMS.headingVelGain
            )
                    .compute(txWorldTarget, pose, robotVelRobot);
            driveCommandWriter.write(new DriveCommandMessage(command));

            MecanumKinematics.WheelVelocities<Time> wheelVels = kinematics.inverse(command);
            double voltage = voltageSensor.getVoltage();

            final MotorFeedforward feedforward = new MotorFeedforward(PARAMS.kS,
                    PARAMS.kV / PARAMS.inPerTick, PARAMS.kA / PARAMS.inPerTick);
            double leftFrontPower = feedforward.compute(wheelVels.leftFront) / voltage;
            double leftBackPower = feedforward.compute(wheelVels.leftBack) / voltage;
            double rightBackPower = feedforward.compute(wheelVels.rightBack) / voltage;
            double rightFrontPower = feedforward.compute(wheelVels.rightFront) / voltage;
            mecanumCommandWriter.write(new MecanumCommandMessage(
                    voltage, leftFrontPower, leftBackPower, rightBackPower, rightFrontPower
            ));

            leftFront.setPower(leftFrontPower);
            leftBack.setPower(leftBackPower);
            rightBack.setPower(rightBackPower);
            rightFront.setPower(rightFrontPower);

            targetHeading = pose.heading.toDouble();

            p.put("x", pose.position.x);
            p.put("y", pose.position.y);
            p.put("heading (deg)", Math.toDegrees(pose.heading.toDouble()));


            p.put("xError", error.position.x);
            p.put("yError", error.position.y);
            p.put("headingError (deg)", Math.toDegrees(error.heading.toDouble()));

            // only draw when active; only one drive action should be active at a time
            Canvas c = p.fieldOverlay();
            drawPoseHistory(c);

            c.setStroke("#4CAF50");
            Drawing.drawRobot(c, txWorldTarget.value());

            c.setStroke("#3F51B5");
            Drawing.drawRobot(c, pose);

            c.setStroke("#4CAF50FF");
            c.setStrokeWidth(1);
            c.strokePolyline(xPoints, yPoints);

            return true;
        }

        @Override
        public void preview(Canvas c) {
            c.setStroke("#4CAF507A");
            c.setStrokeWidth(1);
            c.strokePolyline(xPoints, yPoints);
        }
    }

    public final class TurnAction implements Action {
        private final TimeTurn turn;

        private double beginTs = -1;

        public TurnAction(TimeTurn turn) {
            this.turn = turn;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket p) {
            double t;
            if (beginTs < 0) {
                beginTs = Actions.now();
                t = 0;
            } else {
                t = Actions.now() - beginTs;
            }

            if (t >= turn.duration) {
                leftFront.setPower(0);
                leftBack.setPower(0);
                rightBack.setPower(0);
                rightFront.setPower(0);

                return false;
            }

            Pose2dDual<Time> txWorldTarget = turn.get(t);
            targetPoseWriter.write(new PoseMessage(txWorldTarget.value()));

            PoseVelocity2d robotVelRobot = updatePoseEstimate();

            PoseVelocity2dDual<Time> command = new HolonomicController(
                    PARAMS.axialGain, PARAMS.lateralGain, PARAMS.headingGain,
                    PARAMS.axialVelGain, PARAMS.lateralVelGain, PARAMS.headingVelGain
            )
                    .compute(txWorldTarget, pose, robotVelRobot);
            driveCommandWriter.write(new DriveCommandMessage(command));

            MecanumKinematics.WheelVelocities<Time> wheelVels = kinematics.inverse(command);
            double voltage = voltageSensor.getVoltage();
            final MotorFeedforward feedforward = new MotorFeedforward(PARAMS.kS,
                    PARAMS.kV / PARAMS.inPerTick, PARAMS.kA / PARAMS.inPerTick);
            double leftFrontPower = feedforward.compute(wheelVels.leftFront) / voltage;
            double leftBackPower = feedforward.compute(wheelVels.leftBack) / voltage;
            double rightBackPower = feedforward.compute(wheelVels.rightBack) / voltage;
            double rightFrontPower = feedforward.compute(wheelVels.rightFront) / voltage;
            mecanumCommandWriter.write(new MecanumCommandMessage(
                    voltage, leftFrontPower, leftBackPower, rightBackPower, rightFrontPower
            ));

            leftFront.setPower(feedforward.compute(wheelVels.leftFront) / voltage);
            leftBack.setPower(feedforward.compute(wheelVels.leftBack) / voltage);
            rightBack.setPower(feedforward.compute(wheelVels.rightBack) / voltage);
            rightFront.setPower(feedforward.compute(wheelVels.rightFront) / voltage);

            Canvas c = p.fieldOverlay();
            drawPoseHistory(c);

            c.setStroke("#4CAF50");
            Drawing.drawRobot(c, txWorldTarget.value());

            c.setStroke("#3F51B5");
            Drawing.drawRobot(c, pose);

            c.setStroke("#7C4DFFFF");
            c.fillCircle(turn.beginPose.position.x, turn.beginPose.position.y, 2);

            return true;
        }

        @Override
        public void preview(Canvas c) {
            c.setStroke("#7C4DFF7A");
            c.fillCircle(turn.beginPose.position.x, turn.beginPose.position.y, 2);
        }
    }

    public PoseVelocity2d updatePoseEstimate() {
        Twist2dDual<Time> twist = localizer.update();
        pose = pose.plus(twist.value());

        poseHistory.add(pose);
        while (poseHistory.size() > 100) {
            poseHistory.removeFirst();
        }

        estimatedPoseWriter.write(new PoseMessage(pose));

        return twist.velocity().value();
    }

    private void drawPoseHistory(Canvas c) {
        double[] xPoints = new double[poseHistory.size()];
        double[] yPoints = new double[poseHistory.size()];

        int i = 0;
        for (Pose2d t : poseHistory) {
            xPoints[i] = t.position.x;
            yPoints[i] = t.position.y;

            i++;
        }

        c.setStrokeWidth(1);
        c.setStroke("#3F51B5");
        c.strokePolyline(xPoints, yPoints);
    }

    public TrajectoryActionBuilder actionBuilder(Pose2d beginPose) {
        return new TrajectoryActionBuilder(
                TurnAction::new,
                FollowTrajectoryAction::new,
                new TrajectoryBuilderParams(
                        1e-6,
                        new ProfileParams(
                                0.25, 0.1, 1e-2
                        )
                ),
                beginPose, 0.0,
                defaultTurnConstraints,
                defaultVelConstraint, defaultAccelConstraint
        );
    }
    public void writePos(){
        File file = AppUtil.getInstance().getSettingsFile("x.json");
        ReadWriteFile.writeFile(file, pose.position.x+"");
        file = AppUtil.getInstance().getSettingsFile("y.json");
        ReadWriteFile.writeFile(file, pose.position.y+"");
        file = AppUtil.getInstance().getSettingsFile("h.json");
        ReadWriteFile.writeFile(file, pose.heading.toDouble()+"");
    }
    public void readPos(){
        File file = AppUtil.getInstance().getSettingsFile("x.json");
        double x = new Double(ReadWriteFile.readFile(file)).doubleValue();

        file = AppUtil.getInstance().getSettingsFile("y.json");
        double y = new Double(ReadWriteFile.readFile(file)).doubleValue();

        file = AppUtil.getInstance().getSettingsFile("h.json");
        double h = new Double(ReadWriteFile.readFile(file)).doubleValue();
        pose = new Pose2d(x,y,h);
    }
    ElapsedTime time2 = new ElapsedTime();
    public void AutoGrab(){
        time2.reset();
        double x = 0;
        double y = 0;
        double rotation = 0;
        double cam2inch = 1;
        while(time2.time()<1){
            x = getObjX();
            y = getObjY();
            rotation = getObjRot();
        }
        if(x!=-1 && y!=-1) {
            cam2inch = 1.5 / Math.min(pipeline.target.size.height, pipeline.target.size.width);
            //set line below once servo is configured
            //servo.setPostion((rotation+90)/180);
            Pose2d beginPose = new Pose2d(pose.position.x, pose.position.y, pose.heading.toDouble());
            com.acmerobotics.roadrunner.ftc.Actions.runBlocking(
                    this.actionBuilder(beginPose)
                            .strafeTo(new Vector2d(pose.position.x, (155 - x) * cam2inch), new TranslationalVelConstraint(5.0))
                            //.strafeToConstantHeading(new Vector2d(0, 0), new TranslationalVelConstraint(5.0))
                            .build());
        }else{
            telemetry.addData("No Target", "Found");
        }
    }


    class CameraDetectPipeline extends OpenCvPipeline
    {

        RotatedRect target;
        // switch use HSV values
        // Divide H by 2
        Scalar blueL = new Scalar(200/2, .6*255, .1*255);
        Scalar blueU = new Scalar(260/2, .85*255, 1.00*255);

        //red wraps around the hue spectrum so we need 2 sets of bounds

        Scalar redL1 = new Scalar(345/2, .6*255, .1*255);
        Scalar redU1 = new Scalar(359/2, 1.*255, 1.00*255);
        Scalar redL2 = new Scalar(0, .6*255, .1*255);
        Scalar redU2 = new Scalar(7.5, 1.*255, 1.00*255);

        Scalar yellowL = new Scalar(45/2, .6*255, .1*255);
        Scalar yellowU = new Scalar(60/2, 1*255, 1*255);
        Boolean trackYellow = true;
        @Override
        public Mat processFrame(Mat input)
        {
            Scalar lowerBound;
            Scalar upperBound;

            List<MatOfPoint> contours = new ArrayList<>();
            List<MatOfPoint> contoursYellow = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.cvtColor(input, hierarchy, Imgproc.COLOR_RGB2HSV);

//        // Define range for color bounds in HSV
            if(team.equals("blue")){
                lowerBound = blueL;
                upperBound = blueU;
            }else{
                lowerBound = redL1;
                upperBound = redU1;
            }

//
//        // Create a mask for blue color
            Mat mask = new Mat();
            Core.inRange(hierarchy, lowerBound, upperBound, mask);

            Mat yellowMask = new Mat();
            Core.inRange(hierarchy, yellowL, yellowU, yellowMask);

            if(team.equals("red")){
                Mat redMask = new Mat();
                Core.inRange(hierarchy, redL2, redU2, redMask);
                Core.bitwise_or(mask,redMask,mask);
            }

            Mat objOnly = new Mat();
            Core.bitwise_and(input, input, objOnly, mask);

            //Mat output = objOnly;
            Mat output = input;

//      Insert Code for Rectangle detections here
            Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            List<RotatedRect> minRect = new ArrayList<>();
            for (MatOfPoint contour : contours) {
                if (Imgproc.contourArea(contour) > 100) { // Adjust this threshold as needed
                    RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(contour.toArray()));
                    minRect.add(rect);
                }
            }

            // Draw rectangles and get data
            for (int i = 0; i < minRect.size(); i++) {
                Point[] rectPoints = new Point[4];
                minRect.get(i).points(rectPoints);
                for (int j = 0; j < 4; j++) {
                    Imgproc.line(output, rectPoints[j], rectPoints[(j+1) % 4], new Scalar(0, 255, 0), 2);
                }

                // Get rotation, x, and y for the first rectangle (if any)
                if (i == 0) {
                    target = getClosest(minRect);
                    // You can use these values as needed
                }
            }
            if(trackYellow){//eventually but a conditional here to turn off yellow detection
//                Core.bitwise_or(mask,yellowMask,mask);

                Imgproc.findContours(yellowMask, contoursYellow, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

                List<RotatedRect> minRectYellow = new ArrayList<>();
                for (MatOfPoint contour : contoursYellow) {
                    if (Imgproc.contourArea(contour) > 100) { // Adjust this threshold as needed
                        RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(contour.toArray()));
                        minRectYellow.add(rect);
                    }
                }

                // Draw rectangles and get data
                for (int i = 0; i < minRectYellow.size(); i++) {
                    Point[] rectPoints = new Point[4];
                    minRectYellow.get(i).points(rectPoints);
                    for (int j = 0; j < 4; j++) {
                        Imgproc.line(output, rectPoints[j], rectPoints[(j+1) % 4], new Scalar(0, 255, 0), 2);
                    }

                    // Get rotation, x, and y for the first rectangle (if any)
                    if (i == 0) {
                        target = getClosest(minRect);
                        // You can use these values as needed
                    }
                }
            }
            return output;
        }
        public RotatedRect getClosest(List<RotatedRect> list){
            RotatedRect lowest;
            RotatedRect temp;
            double lowestLength;
            double tempLength;

            if(list.size()>0){
                lowest = list.get(0);
                lowestLength = Math.sqrt(Math.pow(lowest.center.x,2)+Math.pow(lowest.center.y,2));
                for(int i = 1; i < list.size();i++){
                    temp = list.get(i);
                    tempLength = Math.sqrt(Math.pow(temp.center.x,2)+Math.pow(temp.center.y,2));
                    if(tempLength<lowestLength){
                        lowestLength = tempLength;
                        lowest = temp;
                    }
                }
                return lowest;
            }else return null;
        }
        public double getRotation() {
            try {
                if (!target.equals(null)) {
                    Point[] rectPoints = new Point[4];
                    target.points(rectPoints);
                    Point[] topPoints = new Point[2];
                    topPoints[0] = rectPoints[0];
                    topPoints[1] = rectPoints[1];
                    double distance = Math.sqrt(Math.pow(topPoints[0].x - topPoints[1].x, 2) + Math.pow(topPoints[0].y - topPoints[1].y, 2));
                    for (int i = 1; i < 4; i++) {
                        if (distance > Math.sqrt(Math.pow(topPoints[0].x - rectPoints[i].x, 2) + Math.pow(topPoints[0].y - rectPoints[i].y, 2))) {
                            topPoints[1] = rectPoints[i];
                            distance = Math.sqrt(Math.pow(topPoints[0].x - rectPoints[i].x, 2) + Math.pow(topPoints[0].y - rectPoints[i].y, 2));
                        }
                    }
                    double avgX = (topPoints[0].x + topPoints[1].x) / 2;
                    double avgY = (topPoints[0].y + topPoints[1].y) / 2;

                    double angle = 180*(Math.atan((target.center.x - avgX)/(target.center.y - avgY))/Math.PI);
                    return angle;
                }
            }catch(Exception e){
                return 0;
            }
            return 0;
        }


        public double getX() {
            try{
                return target.center.x;
            }catch(Exception e){
                return -1;
            }

        }

        public double getY() {
            try{
                return target.center.y;
            }catch(Exception e) {
                return -1;
            }
        }
    }
    public double getObjX(){
        return pipeline.getX();
    }
    public double getObjY(){
        return pipeline.getY();
    }
    public double getObjRot(){
        return pipeline.getRotation();
    }
    public void setTeamRed(){
        team = "red";
    }
    public void setTeamBlue(){
        team = "blue";
    }
    public void yellowTrackingOn(){
        pipeline.trackYellow = true;
    }
    public void yellowTrackingOff(){
        pipeline.trackYellow = false;
    }
    public void servoTest(){
        extendo.setPosition(1);
        sleep(10000);
        extendo.setPosition(0);
        sleep(10000);
    }
}
