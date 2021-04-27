import boto3
import os
import json
from botocore.exceptions import ClientError

os.environ['LOCALSTACK_ENDPOINT_URL'] = 'http://172.29.64.1:4566'
QUEUE_NAME: str = 'files'
MAX_NUMBER_MESSAGES: int = 1


def get_sqs_client():
    if os.getenv('LOCALSTACK_ENDPOINT_URL'):
        return boto3.client('sqs', endpoint_url=os.environ.get('LOCALSTACK_ENDPOINT_URL'))
    else:
        return boto3.client('sqs')


def get_queue_url(queue_name: str, sqs) -> str:
    response = sqs.get_queue_url(QueueName=queue_name)

    return response['QueueUrl']


def lambda_handler(event, context):
    try:
        sqs = get_sqs_client()
        queue_url: str = get_queue_url(QUEUE_NAME, sqs)

        message = sqs.receive_message(QueueUrl=queue_url, MaxNumberOfMessages=MAX_NUMBER_MESSAGES)
        print(message)
        print(message['Body'])
        if message:
            return {
                'statusCode': 200,
                'body': json.dumps({
                    'message': message
                })
            }

        return {
            'statusCode': 200,
            'body': json.dumps({
                'message': 'Waiting messages',
            })
        }
    except ClientError as e:
        return {
            'statusCode': 500,
            'body': json.dumps({
                'message': e
            })
        }
