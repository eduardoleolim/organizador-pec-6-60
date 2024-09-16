/*
 * Copyright 2023 JetBrains s.r.o.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.jetbrains

import com.jetbrains.WindowDecorations.CustomTitleBar
import java.awt.Dialog
import java.awt.Frame
import java.awt.Window

/**
 * Window decorations consist of title bar, window controls and border.
 * @see CustomTitleBar
 */
interface WindowDecorations {
    /**
     * If `customTitleBar` is not null, system-provided title bar is removed and client area is extended to the
     * top of the frame with window controls painted over the client area.
     * `customTitleBar=null` resets to the default appearance with system-provided title bar.
     * @see [CustomTitleBar]
     *
     * @see [createCustomTitleBar]
     */
    fun setCustomTitleBar(frame: Frame?, customTitleBar: CustomTitleBar?)

    /**
     * If `customTitleBar` is not null, system-provided title bar is removed and client area is extended to the
     * top of the dialog with window controls painted over the client area.
     * `customTitleBar=null` resets to the default appearance with system-provided title bar.
     * @see [CustomTitleBar]
     *
     * @see [createCustomTitleBar]
     */
    fun setCustomTitleBar(dialog: Dialog?, customTitleBar: CustomTitleBar?)

    /**
     * You must [set title bar height][CustomTitleBar.height] before adding it to a window.
     * @see [CustomTitleBar]
     *
     * @see [setCustomTitleBar]
     */
    fun createCustomTitleBar(): CustomTitleBar?

    /**
     * Custom title bar allows merging of window content with native title bar,
     * which is done by treating title bar as part of client area, but with some
     * special behavior like dragging or maximizing on double click.
     * Custom title bar has [height][CustomTitleBar.height] and controls.
     * @implNote Behavior is platform-dependent, only macOS and Windows are supported.
     * @see [setCustomTitleBar]
     */
    interface CustomTitleBar {
        /**
         * @return title bar height, measured in pixels from the top of client area, i.e. excluding top frame border.
         */
        var height: Float

        /**
         * @see [putProperty]
         */
        val properties: Map<String?, Any?>?

        /**
         * @see [putProperty]
         */
        fun putProperties(m: Map<String?, *>?)

        /**
         * Windows & macOS properties:
         *
         *  * `controls.visible` : [Boolean] - whether title bar controls
         * (minimize/maximize/close buttons) are visible, default = true.
         *
         * Windows properties:
         *
         *  * `controls.width` : [Number] - width of block of buttons (not individual buttons).
         * Note that dialogs have only one button, while frames usually have 3 of them.
         *  * `controls.dark` : [Boolean] - whether to use dark or light color theme
         * (light or dark icons respectively).
         *  * `controls.<layer>.<state>` : [Color] - precise control over button colors,
         * where `<layer>` is one of:
         *  * `foreground` * `background`
         * and `<state>` is one of:
         *
         *  * `normal`
         *  * `hovered`
         *  * `pressed`
         *  * `disabled`
         *  * `inactive`
         *
         *
         */
        fun putProperty(key: String?, value: Any?)

        /**
         * @return space occupied by title bar controls on the left (px)
         */
        val leftInset: Float

        /**
         * @return space occupied by title bar controls on the right (px)
         */
        val rightInset: Float

        /**
         * By default, any component which has no cursor or mouse event listeners set is considered transparent for
         * native title bar actions. That is, dragging simple JPanel in title bar area will drag the
         * window, but dragging a JButton will not. Adding mouse listener to a component will prevent any native actions
         * inside bounds of that component.
         *
         *
         * This method gives you precise control of whether to allow native title bar actions or not.
         *
         *  * `client=true` means that mouse is currently over a client area. Native title bar behavior is disabled.
         *  * `client=false` means that mouse is currently over a non-client area. Native title bar behavior is enabled.
         *
         * *Intended usage:
         *
         *  * This method must be called in response to all [mouse events][java.awt.event.MouseEvent]
         * except [java.awt.event.MouseEvent.MOUSE_EXITED] and [java.awt.event.MouseEvent.MOUSE_WHEEL].
         *  * This method is called per-event, i.e. when component has multiple listeners, you only need to call it once.
         *  * If this method hadn't been called, title bar behavior is reverted back to default upon processing the event.
         * *
         * Note that hit test value is relevant only for title bar area, e.g. calling
         * `forceHitTest(false)` will not make window draggable via non-title bar area.
         *
         * <h2>Example:</h2>
         * Suppose you have a `JPanel` in the title bar area. You want it to respond to right-click for
         * some popup menu, but also retain native drag and double-click behavior.
         * <pre>
         * CustomTitleBar titlebar = ...;
         * JPanel panel = ...;
         * MouseAdapter adapter = new MouseAdapter() {
         * private void hit() { titlebar.forceHitTest(false); }
         * public void mouseClicked(MouseEvent e) {
         * hit();
         * if (e.getButton() == MouseEvent.BUTTON3) ...;
         * }
         * public void mousePressed(MouseEvent e) { hit(); }
         * public void mouseReleased(MouseEvent e) { hit(); }
         * public void mouseEntered(MouseEvent e) { hit(); }
         * public void mouseDragged(MouseEvent e) { hit(); }
         * public void mouseMoved(MouseEvent e) { hit(); }
         * };
         * panel.addMouseListener(adapter);
         * panel.addMouseMotionListener(adapter);
        </pre> *
         */
        fun forceHitTest(client: Boolean)

        val containingWindow: Window?
    }
}
