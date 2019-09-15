package com.subhash.aws;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;

public class S3 {

    private static final S3Client DEFAULT_S3_CLIENT = S3Client.builder().build();

    public static void createBucket(String bucketName, Region region) {
        System.out.println("Creating bucket named: " + bucketName + ", region: " + region.id());
        final S3Client s3Client = Region.US_EAST_1.equals(region) ? DEFAULT_S3_CLIENT : S3Client.builder().region(region).build();
        CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                .bucket(bucketName)
                .createBucketConfiguration(
                        Region.US_EAST_1.equals(region) ?
                                CreateBucketConfiguration.builder()
                                        .build() :
                                CreateBucketConfiguration.builder()
                                        .locationConstraint(region.id())
                                    .build()
                ).build();
        CreateBucketResponse bucketResponse = s3Client.createBucket(bucketRequest);
        System.out.println("BucketResponse: " + bucketResponse.toString() );
    }

    public static List<Bucket> listBuckets(Region region) {
        System.out.println("Listing buckets in region: " + region.id());
        final S3Client s3Client = Region.US_EAST_1.equals(region) ? DEFAULT_S3_CLIENT : S3Client.builder().region(region).build();
        ListBucketsRequest bucketsRequest = ListBucketsRequest.builder().build();
        ListBucketsResponse bucketsResponse = s3Client.listBuckets(bucketsRequest);
        System.out.println("Buckets size: " + bucketsResponse.buckets().size());
        bucketsResponse.buckets().forEach(b -> System.out.println("Bucket: " + b.name()));
        return bucketsResponse.buckets();
    }


    public static void deleteBucket(String bucketName, Region region) {
        System.out.println("Deleting bucket named: " + bucketName + ", region: " + region.id());
        final S3Client s3Client = Region.US_EAST_1.equals(region) ? DEFAULT_S3_CLIENT : S3Client.builder().region(region).build();
        DeleteBucketRequest bucketRequest = DeleteBucketRequest.builder().bucket(bucketName).build();
        DeleteBucketResponse bucketResponse = s3Client.deleteBucket(bucketRequest);
        System.out.println("Delete bucket response: " + bucketResponse.toString());
    }

    public static void main(String[] args) {
        Region region = Region.US_EAST_1;
        createBucket("subhash.test.bucket1", region);
        listBuckets(region).forEach(b -> deleteBucket(b.name(), region));
    }
}
