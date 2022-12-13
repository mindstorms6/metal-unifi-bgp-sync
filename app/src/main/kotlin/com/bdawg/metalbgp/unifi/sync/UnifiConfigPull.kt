package com.bdawg.metalbgp.unifi.sync

import com.bdawg.metalbgp.MetalUnfiBgpSyncConfig
import java.io.File
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.xfer.FileSystemFile

class UnifiConfigPull(private val config: MetalUnfiBgpSyncConfig) {
  fun getConfig(): File {
    val ssh = SSHClient()
    ssh.loadKnownHosts()
    ssh.connect(config.unifiConfig.controllerIp)
    try {
      ssh.authPassword(config.unifiConfig.unifiSshUsername, config.unifiConfig.unifiSshPassword)
      val path = "/tmp/config.json"
      ssh.newSCPFileTransfer()
          .download(config.unifiConfig.unifiConfigLocation, FileSystemFile(path))
      return File(path)
    } finally {
      ssh.disconnect()
    }
  }

  fun putConfig(localFile: File) {
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
