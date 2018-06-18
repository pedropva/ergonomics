package com.projecttango.examples.java.pointtopoint.Classification;


class Math
{
    public static double Point3Y0GetAngleBetween(Joint SkeletonV1FirstPoint, Joint SkeletonV1SecondPoint, Joint SkeletonV2FirstPoint, Joint SkeletonV2SecondPoint)
    {
        //Mudar para calcular a projeção dos vetores no plano XZ

        Vector3D PointCenter = new Vector3D(SkeletonV1FirstPoint.x, 0, SkeletonV1FirstPoint.z);
        Vector3D Point2 = new Vector3D(SkeletonV1SecondPoint.x, 0, SkeletonV1SecondPoint.z);

        Vector3D Point3 = new Vector3D(SkeletonV2FirstPoint.x, 0, SkeletonV2FirstPoint.z);
        Vector3D Point4 = new Vector3D(SkeletonV2SecondPoint.x, 0, SkeletonV2SecondPoint.z);


        Vector3D Vector1 = new Vector3D();
        Vector3D Vector2 = new Vector3D();

        Vector1 = PointCenter.Subtract(Point2);
        Vector2 = Point3.Subtract(Point4);

        return Vector1.AngleToGraus(Vector2);
    }

    public static double Point3Z0GetAngleBetween( Joint SkeletonFirstPoint, Joint SkeletonSecondPoint)
    {
        //XMLSkeletonPointHipCenter, XMLSkeletonPointShoulderCenter)

        Vector3D PointCenter = new Vector3D(SkeletonFirstPoint.x, SkeletonFirstPoint.y, SkeletonFirstPoint.z);
        Vector3D Point2 = new Vector3D(SkeletonSecondPoint.x, SkeletonSecondPoint.y, SkeletonSecondPoint.z);
        Vector3D Point3 = new Vector3D(SkeletonFirstPoint.x, SkeletonFirstPoint.y, 0);

        Vector3D Vector1 = new Vector3D();
        Vector3D Vector2 = new Vector3D();

        Vector1 = PointCenter.Subtract(Point2);
        Vector2 = PointCenter.Subtract(Point3);
        return Vector1.AngleToGraus(Vector2);
    }
}
