/*
{
    class Math
    {
        public static double Point3Y0GetAngleBetween(ref SkeletonPoint SkeletonV1FirstPoint, ref SkeletonPoint SkeletonV1SecondPoint,ref SkeletonPoint SkeletonV2FirstPoint, ref SkeletonPoint SkeletonV2SecondPoint)
        {
            //Mudar para calcular a projeção dos vetores no plano XZ

            Vector3D PointCenter = new Vector3D(SkeletonV1FirstPoint.X, 0, SkeletonV1FirstPoint.Z);
            Vector3D Point2 = new Vector3D(SkeletonV1SecondPoint.X, 0, SkeletonV1SecondPoint.Z);

            Vector3D Point3 = new Vector3D(SkeletonV2FirstPoint.X, 0, SkeletonV2FirstPoint.Z);
            Vector3D Point4 = new Vector3D(SkeletonV2SecondPoint.X, 0, SkeletonV2SecondPoint.Z);


            Vector3D Vector1 = new Vector3D();
            Vector3D Vector2 = new Vector3D();

            Vector1 = PointCenter.Subtract(Point2);
            Vector2 = Point3.Subtract(Point4);

            return Vector1.AngleToGraus(Vector2);
        }

        public static double Point3Z0GetAngleBetween(ref SkeletonPoint SkeletonFirstPoint, ref SkeletonPoint SkeletonSecondPoint)
        {
            //XMLSkeletonPointHipCenter, XMLSkeletonPointShoulderCenter)

            Vector3D PointCenter = new Vector3D(SkeletonFirstPoint.X, SkeletonFirstPoint.Y, SkeletonFirstPoint.Z);
            Vector3D Point2 = new Vector3D(SkeletonSecondPoint.X, SkeletonSecondPoint.Y, SkeletonSecondPoint.Z);
            Vector3D Point3 = new Vector3D(SkeletonFirstPoint.X, SkeletonFirstPoint.Y, 0);

            Vector3D Vector1 = new Vector3D();
            Vector3D Vector2 = new Vector3D();

            Vector1 = PointCenter.Subtract(Point2);
            Vector2 = PointCenter.Subtract(Point3);
            return Vector1.AngleToGraus(Vector2);
        }
    }
}
*/