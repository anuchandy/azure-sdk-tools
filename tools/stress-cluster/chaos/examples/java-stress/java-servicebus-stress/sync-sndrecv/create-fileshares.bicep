param fileShareUniquePrefix string
param fileSharesCount int

resource storageFileShare 'Microsoft.Storage/storageAccounts/fileServices/shares@2021-04-01' = [for i in range(0, fileSharesCount): {
  name: 'podcontainerslogs/default/${fileShareUniquePrefix}${i}'
  properties: {
  }
}]
