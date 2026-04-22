# Design System Document: The Temporal Monolith

## 1. Overview & Creative North Star
**Creative North Star: The Living Monolith**
This design system rejects the "dashboard" clutter of traditional utility apps. Instead, it treats time as a physical, atmospheric presence. The UI is a "Living Monolith"—a singular, high-contrast focal point that shifts its cellular structure and aura as the countdown progresses. We break the template through **Intentional Asymmetry** and **Scale Shock**. By placing massive, editorial-grade typography against deep, void-like surfaces, we create a sense of cinematic tension and focus.

The experience is not just about reading a clock; it is about feeling the transition of time through tonal shifts and spatial breathing room.

---

## 2. Colors & Atmospheric Shift
The palette is built on a deep `surface` (#131313) to ensure the timer remains the undisputed protagonist.

### The Dynamic State Logic
The app’s environment must morph based on the speaker’s status:
- **On Track:** Dominated by `primary` (#5af0b3). A calm, forest-inspired glow.
- **Approaching End:** Subtle transition to `secondary` (#ffb95f). An amber warning that feels urgent but not panicked.
- **Time Up:** A hard shift to `error` (#ffb4ab) and `tertiary` (#ffcac5). High-frequency alert tones.

### Surface Hierarchy & The "No-Line" Rule
**Explicit Instruction:** Do not use 1px solid borders to define sections.
- **Boundaries via Value:** Separate the navigation from the timer using `surface-container-low` (#1C1B1B) against the `background` (#131313). 
- **Nesting:** Treat the UI as layers of fine paper. A "Control Card" should not have an outline; it should be a `surface-container-high` (#2A2A2A) shape sitting on a `surface-container` (#201F1F) base.
- **Glassmorphism:** For floating overlays (like settings or "End Session" modals), use `surface-bright` (#3A3939) with a 20% opacity and a `backdrop-blur` of 16px. This creates a "frosted glass" effect that feels premium and integrated.

---

## 3. Typography: Editorial Authority
We utilize a high-contrast pairing to distinguish between "Data" and "Instruction."

- **The Display Scale (`spaceGrotesk`):** Used for the timer and primary headlines. This typeface provides a technical, precise, and avant-garde feel. The `display-lg` (3.5rem) should be used aggressively—don't be afraid to let the timer dominate 60% of the screen.
- **The Body Scale (`manrope`):** Used for labels, settings, and secondary information. Its humanist qualities offer a soft counterpoint to the rigid precision of the timer.

**Hierarchy Strategy:** Use `label-sm` in all-caps with increased letter spacing (0.05rem) for "Status" indicators (e.g., "REMAINING") to give the app a curated, magazine-style layout.

---

## 4. Elevation & Depth
Depth is achieved through **Tonal Layering** rather than traditional drop shadows.

- **The Layering Principle:** Stacking `surface-container-lowest` on top of `surface-container-high` creates a natural "inset" or "lifted" look. 
- **Ambient Shadows:** Shadows are a last resort. If used, they must be "Atmospheric": 32px blur, 4% opacity, using a tint of `primary` or `secondary` (depending on the current state) to mimic the glow of the timer on a dark surface.
- **The Ghost Border:** For interactive elements like input fields, use the `outline-variant` (#3C4A42) at 15% opacity. It should be felt, not seen.

---

## 5. Components

### The Monolith Timer (Custom Component)
- **Visuals:** Massive `display-lg` typography. 
- **Interaction:** Use a subtle `primary-container` (#34D399) gradient glow behind the text that pulses slowly (2-second ease-in-out) to indicate the app is active.

### Buttons (The Tactile Core)
- **Primary:** No borders. Background: `primary` (#5af0b3). Text: `on-primary` (#003825). Shape: `xl` (0.75rem) for a modern, chunky feel.
- **Secondary (Action):** `surface-container-highest` (#353534) background with `on-surface` (#E5E2E1) text.
- **Ghost:** No background. `outline` (#85948B) text. Use for low-priority actions like "Reset."

### Progress "Aura" (Custom Component)
- Instead of a standard horizontal bar, use a 4px tall line at the very top of the viewport. 
- Color transitions from `primary` to `secondary` as the percentage of time elapsed increases.

### Input Fields
- **Container:** `surface-container-low` (#1C1B1B).
- **Border:** `none`.
- **Active State:** A bottom-only "Ghost Border" using `primary` at 40% opacity.

---

## 6. Do’s and Don’ts

### Do:
- **Embrace Negative Space:** Keep the edges of the screen empty. Force the user’s eye to the center or a specific asymmetric anchor.
- **Use "Scale Shock":** Make the timer significantly larger than you think it should be.
- **Transitions:** Every color change must be a "Cross-Fade" (min 500ms). The UI should "bleed" from green to amber.

### Don’t:
- **Don’t use Dividers:** Never use a line to separate "Time Remaining" from "Current Task." Use a 48px vertical spacer instead.
- **Don’t use Pure White:** Use `on-surface` (#E5E2E1) for text to prevent eye strain in dark environments.
- **Don’t use Standard Shadows:** Avoid the "Default Android/iOS Shadow." It breaks the premium editorial feel. Use Tonal Layering.

---

## 7. Roundedness Scale
- **Containers/Cards:** `xl` (0.75rem).
- **Buttons/Chips:** `full` (9999px) for a pill-shaped, organic feel.
- **Modals:** `lg` (0.5rem) on top corners only to ground them to the bottom of the screen.

---

*Director's Final Note: This design system is about the "Vibe" of professional confidence. Every pixel must feel like it was placed with a surgical scalpel. If an element doesn't serve the timer, remove it.*