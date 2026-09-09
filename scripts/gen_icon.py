#!/usr/bin/env python3
"""Generate the JXL launcher icons: white background, "JXL" in
Google-logo colors (J=red, X=green, L=blue), Arial Rounded Bold."""
from PIL import Image, ImageDraw, ImageFont

# Google brand colors
RED, GREEN, BLUE = "#EA4335", "#34A853", "#4285F4"
FONT_PATH = "/System/Library/Fonts/Supplemental/Arial Rounded Bold.ttf"
LETTERS = [("J", RED), ("X", GREEN), ("L", BLUE)]

RES = "app/src/main/res"


def render_text(target_w: int, text_w_frac: float, ss: int = 4):
    """Transparent canvas target_w x target_w (supersampled), "JXL" laid
    out letter-by-letter, total text width = text_w_frac * canvas,
    vertically centered. Returns the supersampled RGBA image."""
    S = target_w * ss
    font = ImageFont.truetype(FONT_PATH, 256)
    # Measure per-letter advance/bbox at a fixed size, then scale to fit.
    boxes = []
    for ch, _ in LETTERS:
        bbox = font.getbbox(ch)
        boxes.append(bbox)
    total_w = sum(b[2] - b[0] for b in boxes)
    # Account for per-letter left bearings: lay out with cumulative offsets.
    text_px = S * text_w_frac
    scale = text_px / total_w
    font_h = int(256 * scale)
    font = ImageFont.truetype(FONT_PATH, font_h)
    boxes = [font.getbbox(ch) for ch, _ in LETTERS]
    total_w = sum(b[2] - b[0] for b in boxes)
    asc, desc = font.getmetrics()
    text_h = asc + desc

    img = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    x = (S - total_w) / 2
    # Vertical: center the cap-height band rather than the full line box.
    top = min(b[1] for b in boxes)
    bot = max(b[3] for b in boxes)
    y = (S - (bot - top)) / 2 - top
    for (ch, color), b in zip(LETTERS, boxes):
        d.text((x - b[0], y), ch, font=font, fill=color)
        x += b[2] - b[0]
    return img


def save_scaled(img: Image.Image, out_path: str, size: int):
    out = img.resize((size, size), Image.LANCZOS)
    out.save(out_path)
    print("wrote", out_path, size)


# --- Adaptive icon foreground (108dp canvas). The conservative 66dp "safe
# zone" circle is radius 0.611 of the half-canvas; at width 0.62 the text
# corners reach ~0.65, still inside the common circular masks (72dp diameter,
# radius 0.667) but slightly beyond the conservative zone.
foreground = render_text(1024, 0.62)
for dpi, dp in [("mdpi", 108), ("hdpi", 162), ("xhdpi", 216),
                ("xxhdpi", 324), ("xxxhdpi", 432)]:
    save_scaled(foreground, f"{RES}/mipmap-{dpi}/ic_launcher_foreground.png", dp)

# --- Legacy full-bleed launcher icons: white square + larger text.
legacy = Image.new("RGBA", (1024, 1024), (255, 255, 255, 255))
txt = render_text(1024, 0.76).resize((1024, 1024), Image.LANCZOS)
legacy.alpha_composite(txt)
for dpi, dp in [("mdpi", 48), ("hdpi", 72), ("xhdpi", 96),
                ("xxhdpi", 144), ("xxxhdpi", 192)]:
    save_scaled(legacy, f"{RES}/mipmap-{dpi}/ic_launcher.png", dp)

print("done")
