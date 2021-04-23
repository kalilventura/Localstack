console.log('Iniciando Lambda')

exports.handler = function (event, context) {
  const message = "Hello World";
  context.succeed(message);
}