package com.bdawg.metalbgp.unifi.sync

import com.bdawg.metalbgp.MetalUnfiBgpSyncConfig
import java.io.File
import java.util.UUID
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.xfer.FileSystemFile

class UnifiConfigPull(private val config: MetalUnfiBgpSyncConfig) {
  fun getControllerConfig(): File {
    val ssh = SSHClient()
    ssh.loadKnownHosts()
    ssh.connect(config.unifiConfig.controllerIp)
    try {
      ssh.authPassword(config.unifiConfig.unifiSshUsername, config.unifiConfig.unifiSshPassword)
      val path = "/tmp/${UUID.randomUUID().toString()}-config.gateway.json"
      ssh.newSCPFileTransfer()
          .download(config.unifiConfig.unifiConfigLocation, FileSystemFile(path))
      return File(path)
    } finally {
      ssh.disconnect()
    }
  }

  fun putControllerConfig(localFile: File) {
    val ssh = SSHClient()
    ssh.loadKnownHosts()
    ssh.connect(config.unifiConfig.controllerIp)
    try {
      ssh.authPassword(config.unifiConfig.unifiSshUsername, config.unifiConfig.unifiSshPassword)
      ssh.newSCPFileTransfer()
          .upload(FileSystemFile(localFile.absolutePath), config.unifiConfig.unifiConfigLocation)
    } finally {
      ssh.disconnect()
    }
  }
}
