const { SQS } = require("aws-sdk");
const { promisify } = require('util');

const sqs = new SQS();

sqs.receiveMessage = promisify(sqs.receiveMessage);

const QueueUrl = process.env.QUEUE_URL || 'http://localhost:4566/000000000000/files';

const receiveParams = {
  QueueUrl,
  MaxNumberOfMessages: 1
};

const receive = async (event) => {
  try {
    const queueData = sqs.receiveMessage(receiveParams);

    if (queueData && queueData.Messages && queueData.Messages.length > 0) {
      const [firstMessage] = queueData.Messages;

      const deleteParams = {
        QueueUrl,
        ReceiptHandle: firstMessage.ReceiptHandle
      };
      sqs.deleteMessage(deleteParams);

      return { statusCode: 200, body: JSON.stringify({ message: firstMessage }) }

    } else {
      return { statusCode: 204 }
    }
  } catch (e) {
    return { statusCode: 500, body: JSON.stringify({ message: e.message }) }
  }
}

module.exports = {
  receive
};
