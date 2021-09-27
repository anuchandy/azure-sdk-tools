@description('The base resource name.')
param baseName string = resourceGroup().name

// @description('The client OID to grant access to test resources.')
// param testApplicationOid string

@description('The location of the resources. By default, this is the same as the resource group.')
param location string = resourceGroup().location

// var contributorRoleId = 'b24988ac-6180-42a0-ab88-20f7382dd24c'
// var serviceBusDataOwnerRoleId = '090c5cfd-751d-490a-894a-3ce6f1109419'
var serviceBusNamespaceName = 'sb-${baseName}-msging'
var serviceBusAuthRuleName = 'sb-${baseName}-msging-auth-rule'
var serviceBusQueueName = 'sb-${baseName}-msging-queue1'

resource serviceBusNamespace 'Microsoft.ServiceBus/namespaces@2017-04-01' = {
  name: serviceBusNamespaceName
  location: location
  sku: {
    name: 'Standard'
  }
  properties: {}
}

resource serviceBusAuthRule 'Microsoft.ServiceBus/namespaces/authorizationRules@2017-04-01' = {
  name: serviceBusAuthRuleName
  properties: {
    Rights: [
      'Manage'
      'Send'
      'Listen'
    ]
  }
  dependsOn: [
    serviceBusNamespace
  ]
}

resource serviceBusQueue 'Microsoft.ServiceBus/namespaces/queues@2017-04-01' = {
  name: serviceBusQueueName
  properties: {
    lockDuration: 'PT5M'
    maxSizeInMegabytes: 1024
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

// resource id_name_baseName_serviceBusDataOwnerRoleId_testApplicationOid 'Microsoft.Authorization/roleAssignments@2019-04-01-preview' = {
//   name: guid(resourceGroup().id, deployment().name, baseName, serviceBusDataOwnerRoleId, testApplicationOid)
//   properties: {
//     roleDefinitionId: resourceId('Microsoft.Authorization/roleDefinitions', serviceBusDataOwnerRoleId)
//     principalId: testApplicationOid
//     scope: resourceGroup().id
//   }
// }

// resource id_name_baseName_contributorRoleId_testApplicationOid 'Microsoft.Authorization/roleAssignments@2019-04-01-preview' = {
//   name: guid(resourceGroup().id, deployment().name, baseName, contributorRoleId, testApplicationOid)
//   properties: {
//     roleDefinitionId: resourceId('Microsoft.Authorization/roleDefinitions', contributorRoleId)
//     principalId: testApplicationOid
//     scope: resourceGroup().id
//   }
// }

output SERVICE_BUS_NAMESPACE string = serviceBusNamespaceName
output SERVICE_BUS_HOSTNAME string = '${serviceBusNamespaceName}.servicebus.windows.net'
output SERVICE_BUS_CONN_STR string = listkeys(serviceBusAuthRuleName, '2017-04-01').primaryConnectionString
output SERVICE_BUS_QUEUE_NAME string = serviceBusQueueName
output SERVICE_BUS_SAS_POLICY string = serviceBusAuthRuleName
output SERVICE_BUS_SAS_KEY string = listkeys(serviceBusAuthRuleName, '2017-04-01').primaryKey
