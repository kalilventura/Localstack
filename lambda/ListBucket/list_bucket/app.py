import json
import os
import boto3
from botocore.exceptions import ClientError

os.environ['LOCALSTACK_ENDPOINT_URL'] = 'http://172.18.112.1:4566'


def get_s3_client():
    if os.environ.get('LOCALSTACK_ENDPOINT_URL'):
        return boto3.client("s3", endpoint_url=os.environ.get('LOCALSTACK_ENDPOINT_URL'))
    else:
        return boto3.client("s3")


def lambda_handler(event, context):
    try:
        s3 = get_s3_client()

        buckets = []
        response = s3.list_buckets()
        for bucket in response['Buckets']:
            current = bucket["Name"]
            print(f'Bucket name: {current}')
            buckets.append(current)

        return {
            'statusCode': 200,
            'body': json.dumps({
                'message': 'found buckets',
                'buckets': buckets
            })
        }
    except ClientError as e:
        return {
            'statusCode': 500,
            'body': json.dumps({
                'message': e
            })
        }
