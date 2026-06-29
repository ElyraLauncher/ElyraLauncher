// Gradle-only compat stub for the internal AOSP MSDL haptics API.
// MSDLPlayer is a Kotlin class; its companion object is accessed from Java as
// MSDLPlayer.Companion.createPlayer(...). This stub mirrors that structure.
package com.google.android.msdl.domain;

import android.os.Vibrator;
import com.google.android.msdl.data.model.MSDLToken;
import com.google.android.msdl.logging.MSDLEvent;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

public abstract class MSDLPlayer {

    public static final Companion Companion = new Companion();

    public abstract void playToken(MSDLToken token, InteractionProperties properties);
    public abstract List<MSDLEvent> getHistory();

    public static class Companion {
        public MSDLPlayer createPlayer(
                Vibrator vibrator,
                Executor executor,
                Object useHapticFeedbackForToken) {
            return new MSDLPlayer() {
                @Override
                public void playToken(MSDLToken token, InteractionProperties properties) {}
                @Override
                public List<MSDLEvent> getHistory() {
                    return Collections.emptyList();
                }
                @Override
                public String toString() {
                    return "MSDLPlayer[stub]";
                }
            };
        }
    }
}
