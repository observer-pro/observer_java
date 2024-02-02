package pro.sky.observer_java.scheduler;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import pro.sky.observer_java.constants.CustomSocketEvents;
import pro.sky.observer_java.constants.StepStatus;
import pro.sky.observer_java.mapper.JsonMapper;
import pro.sky.observer_java.model.Step;
import pro.sky.observer_java.resources.ResourceManager;

public class InProgressSchedulesAndSending implements Runnable {
    public void run() {

        if (ResourceManager.getInstance().getStepsMap().isEmpty()) {
            return;
        }

        VirtualFile apiDir = ResourceManager.getInstance().getToolWindow().getProject().getBaseDir();
        VfsUtil.markDirtyAndRefresh(true, true, true, apiDir);

        Step currentStep = ResourceManager.getInstance().getConnectedPanel().getCurrentlySelectedStep();

        if (!ResourceManager.getInstance().getInProgress()) {

            apiDir.refresh(false, true);
            ApplicationManager.getApplication().invokeAndWait(() -> {
                FileDocumentManager.getInstance().saveAllDocuments();
            });
            if (currentStep.getStatus().equals(StepStatus.IN_PROGRESS)) {
                currentStep.setStatus(StepStatus.NONE);
                ResourceManager.getInstance().getStepsMap().put(currentStep.getName(), currentStep);
            }

            ResourceManager.getInstance().getmSocket().emit(CustomSocketEvents.STEPS_STATUS_TO_MENTOR,
                    JsonMapper.stepStatusToJson(ResourceManager.getInstance().getStepsMap()));
            return;
        }


        if (currentStep.getStatus().equals(StepStatus.NONE)) {
            currentStep.setStatus(StepStatus.IN_PROGRESS);
            ResourceManager.getInstance().getStepsMap().put(currentStep.getName(), currentStep);

            ResourceManager.getInstance().getmSocket().emit(CustomSocketEvents.STEPS_STATUS_TO_MENTOR,
                    JsonMapper.stepStatusToJson(ResourceManager.getInstance().getStepsMap()));
        }
        ResourceManager.getInstance().setInProgressFlag(false);
    }
}
