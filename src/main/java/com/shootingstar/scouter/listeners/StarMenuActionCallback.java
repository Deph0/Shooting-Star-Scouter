package com.shootingstar.scouter.listeners;

import javax.swing.JOptionPane;

import com.google.inject.Injector;
import com.shootingstar.scouter.ShootingStarPanel;
import com.shootingstar.scouter.models.StarActionType;
import com.shootingstar.scouter.models.StarData;
import com.shootingstar.scouter.services.StarDataService;
import com.shootingstar.scouter.websocket.MessageBuilder;
import com.shootingstar.scouter.websocket.WebSocketManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StarMenuActionCallback
{
    private final ShootingStarPanel panel;
    private final Injector injector;
    private final WebSocketManager webSocketManager;

    public StarMenuActionCallback(ShootingStarPanel panel)
    {
        this.panel = panel;
        this.injector = panel.getInjector();
        this.webSocketManager = panel.getWebSocketManager();
    }

    /**
     * Handle action button clicks from the current stars table
     */
    public void handleStarAction(String world, StarActionType action)
    {
        StarDataService starDataService = injector.getInstance(StarDataService.class);
        
        switch (action) {
            case REMOVE:
                String removeMessage = MessageBuilder.buildStarRemove(world);
                webSocketManager.sendMessage(removeMessage);
                starDataService.removeStar(world);
                panel.refreshStars();
                break;
                
            case TOGGLE_BACKUP:
                starDataService.getStar(world).ifPresent(currentStar -> {
                    log.debug("Current star before toggle: {}", currentStar);
                    // Toggle backup status
                    boolean newBackupStatus = !currentStar.isBackup();
                    
                    StarData newStarData = currentStar.withBackup(newBackupStatus);

                    // Update cache
                    starDataService.addOrUpdate(newStarData);

                    // Send update to server
                    String updateMessage = MessageBuilder.buildStarUpdate(newStarData);
                    webSocketManager.sendMessage(updateMessage);

                    panel.refreshStars();
                });
                break;
                
            case EDIT:
                showEditStarDialog(world);
                break;
        }
    }
    
    /**
     * Show dialog to edit star details
     */
    private void showEditStarDialog(String world)
    {
        // TODO: Create a proper edit dialog with fields for world, tier, location, backup
        JOptionPane.showMessageDialog(panel,
            "Edit star dialog for world " + world + "\n(Advanced feature - to be implemented)",
            "Edit Star", JOptionPane.INFORMATION_MESSAGE);
   
    }
    
}
