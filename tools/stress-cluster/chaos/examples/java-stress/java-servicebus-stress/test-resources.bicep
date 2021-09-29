@description('The base resource name.')
param baseName string = resourceGroup().name

param testApplicationOid string = ''

var apiVersion = '2017-04-01'
var location = resourceGroup().location

// The parent service bus resource name.
var serviceBusNamespaceName = 'sb-${baseName}-msging'
// The child resource names.
var serviceBusAuthRuleName = 'RootManageSharedAccessKey'
var serviceBusQueueName = 'simple-queue'
var serviceBusSessionQueueName = 'session-queue'
// The child resource name must be scoped when referenced from bicep resource declaration.
// https://docs.microsoft.com/en-us/azure/azure-resource-manager/bicep/child-resource-name-type
var serviceBusScopedAuthRuleName = '${serviceBusNamespaceName}/${serviceBusAuthRuleName}'
var serviceBusScopedQueueName = '${serviceBusNamespaceName}/${serviceBusQueueName}'
var serviceBusScopedSessionQueueName = '${serviceBusNamespaceName}/${serviceBusSessionQueueName}'

resource serviceBusNamespace 'Microsoft.ServiceBus/namespaces@2017-04-01' = {
  name: serviceBusNamespaceName
  location: location
  sku: {
    name: 'Standard'
    tier: 'Standard'
  }
  properties: {
  }
}

resource serviceBusAuthRule 'Microsoft.ServiceBus/namespaces/AuthorizationRules@2017-04-01' = {
  name: serviceBusScopedAuthRuleName
  properties: {
    rights: [
      'Listen'
      'Manage'
      'Send'
    ]
  }
  dependsOn: [
    serviceBusNamespace
  ]
}

resource serviceBusQueue 'Microsoft.ServiceBus/namespaces/queues@2017-04-01' = {
  name: serviceBusScopedQueueName
  properties: {
    lockDuration: 'PT5M'
    maxSizeInMegabytes: 5120
    requiresDuplicateDetection: false
    requiresSession: false
    defaultMessageTimeToLive: 'P10675199DT2H48M5.4775807S'
    deadLetteringOnMessageExpiration: false
    duplicateDetectionHistoryTimeWindow: 'PT10M'
    maxDeliveryCount: 10
    autoDeleteOnIdle: 'P10675199DT2H48M5.4775807S'
    enablePartitioning: false
    enableExpress: false
  }
  dependsOn: [
    serviceBusNamespace
  ]
}

resource serviceBusSessionQueue 'Microsoft.ServiceBus/namespaces/queues@2017-04-01' = {
  name: serviceBusScopedSessionQueueName
  properties: {
    lockDuration: 'PT5M'
    maxSizeInMegabytes: 5120
    requiresDuplicateDetection: false
    requiresSession: true
    defaultMessageTimeToLive: 'P10675199DT2H48M5.4775807S'
    deadLetteringOnMessageExpiration: false
    duplicateDetectionHistoryTimeWindow: 'PT10M'
    maxDeliveryCount: 10
    autoDeleteOnIdle: 'P10675199DT2H48M5.4775807S'
    enablePartitioning: false
    enableExpress: false
  }
  dependsOn: [
    serviceBusNamespace
  ]
}

output AZURE_CLIENT_OID string = testApplicationOid
output SERVICEBUS_ENDPOINT string = replace(serviceBusNamespace.properties.serviceBusEndpoint, ':443/', '')
output SERVICE_BUS_CONN_STR string = listkeys(serviceBusAuthRule.id, apiVersion).primaryConnectionString
output SERVICE_BUS_QUEUE_NAME string = serviceBusQueueName
output SERVICE_BUS_SESSION_QUEUE_NAME string = serviceBusSessionQueueName
