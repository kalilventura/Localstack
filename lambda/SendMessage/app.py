import boto3
import json
import logging
from datetime import datetime

QUEUE_NAME: str = 'files'
LOCALSTACK_HOST: str = 'http://172.30.128.1:4566'


class HttpUtils:
    @staticmethod
    def response(err=None, code=404, res=None):
        message = err if err else res
        body = json.dumps(message)

        logging.info(f'Send response: code {code}, body: {body}')

        return {
            'statusCode': str(code),
            'body': body,
            'headers': {
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': '*'
            }
        }


def get_sqs_client():
    try:
        return boto3.client('sqs', endpoint_url=LOCALSTACK_HOST)
    except Exception as e:
        logging.error(f'Error when trying to create sqs client: {e}')
        raise e


def get_queue_url(queue_name: str, queue) -> str:
    try:
        response = queue.get_queue_url(QueueName=queue_name)
        logging.info(f'QueueUrl: {response["QueueUrl"]}')
        return response['QueueUrl']
    except Exception as e:
        logging.error(f'Error when trying to get queue url: {e}')
        raise e


def send_message(queue, message_body, message_attributes=None):
    try:
        queue_url: str = get_queue_url(queue_name=QUEUE_NAME, queue=queue)

        if not message_attributes:
            message_attributes = {}

        response = queue.send_message(
            QueueUrl=queue_url,
            MessageBody=json.dumps(message_body),
            MessageAttributes=message_attributes
        )

        message = {
            'MessageId': response['MessageId'],
            'MD5OfMessageBody': response['MD5OfMessageBody'],
            'date': str(datetime.now())
        }
        logging.info(f'Message: {message}')
        return message
    except Exception as e:
        logging.error(f'Error when trying to send a message: {e}')
        raise e


def lambda_handler(event, context):
    try:
        if event['httpMethod'] != 'POST' or event['body'] is None:
            response = {'message': 'Bad Request'}
            return HttpUtils.response(code=400, res=response)

        request = json.loads(event['body'])
        logging.info(f'request body: {request}')

        queue = get_sqs_client()
        response = send_message(queue, request)

        return HttpUtils.response(code=200, res=response)
    except Exception as e:
        return HttpUtils.response(code=500, err=e)
