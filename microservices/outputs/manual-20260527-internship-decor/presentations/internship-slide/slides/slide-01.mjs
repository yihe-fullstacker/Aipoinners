const C = {
  green: "#5FA439",
  deepGreen: "#0B6939",
  midGreen: "#3F7E24",
  lightGreen: "#A9D690",
  mint: "#8DF0C2",
  softMint: "#D9F2DB",
  gold: "#E7B63E",
  black: "#111111",
  white: "#FFFFFF",
  paper: "#FFFDFC",
};

function shape(ctx, slide, x, y, w, h, fill, line = ctx.line("#00000000", 0), geometry = "rect", name) {
  return ctx.addShape(slide, { x, y, w, h, fill, line, geometry, name });
}

function text(ctx, slide, value, x, y, w, h, opts = {}) {
  return ctx.addText(slide, {
    text: value,
    x,
    y,
    w,
    h,
    fontSize: opts.fontSize ?? 24,
    color: opts.color ?? C.black,
    bold: opts.bold ?? false,
    typeface: opts.typeface ?? "Microsoft YaHei",
    align: opts.align ?? "left",
    valign: opts.valign ?? "top",
    fill: opts.fill ?? "#00000000",
    line: opts.line ?? { style: "solid", fill: "#00000000", width: 0 },
    insets: opts.insets ?? { left: 0, right: 0, top: 0, bottom: 0 },
    name: opts.name,
  });
}

function line(ctx, slide, x, y, w, h, color, width = 2, name) {
  return shape(ctx, slide, x, y, w, h, "#00000000", ctx.line(color, width), "line", name);
}

function addLeaf(ctx, slide, x, y, w, h, rotate = 0, fill = "#CBE8BF66") {
  const leaf = shape(ctx, slide, x, y, w, h, fill, ctx.line("#7DBA5B66", 1), "ellipse", "decor-leaf");
  leaf.rotation = rotate;
  line(ctx, slide, x + w * 0.24, y + h * 0.5, w * 0.52, 0, "#7DBA5B99", 1, "decor-leaf-vein").rotation = rotate;
}

function addSpark(ctx, slide, cx, cy, s, color = "#E5B43A66") {
  line(ctx, slide, cx - s, cy, s * 2, 0, color, 1.2, "decor-spark-h");
  line(ctx, slide, cx, cy - s, 0, s * 2, color, 1.2, "decor-spark-v");
}

function addDecorations(ctx, slide) {
  shape(ctx, slide, 18, 116, 210, 3, "#78B95755", ctx.line("#00000000", 0), "roundRect", "decor-title-glow");
  shape(ctx, slide, 1052, 103, 174, 3, "#78B95755", ctx.line("#00000000", 0), "roundRect", "decor-top-rule");
  shape(ctx, slide, 1230, 126, 48, 48, "#A7D98A33", ctx.line("#6EAD4F55", 1), "ellipse", "decor-corner-arc");
  shape(ctx, slide, 1019, 575, 100, 100, "#8ACD7233", ctx.line("#6EAD4F66", 1), "ellipse", "decor-bottom-arc");
  addLeaf(ctx, slide, 112, 106, 44, 16, -18);
  addLeaf(ctx, slide, 473, 117, 36, 13, 22);
  addLeaf(ctx, slide, 1018, 192, 42, 15, -34);
  addLeaf(ctx, slide, 1436, 110, 52, 18, 28);
  addLeaf(ctx, slide, 414, 642, 46, 16, 18);
  addSpark(ctx, slide, 539, 100, 10);
  addSpark(ctx, slide, 1076, 232, 8, "#FFFFFFAA");
  addSpark(ctx, slide, 1470, 650, 10, "#FFFFFF99");
  shape(ctx, slide, 481, 205, 82, 4, "#FFFFFF88", ctx.line("#00000000", 0), "roundRect", "decor-card-glint-1");
  shape(ctx, slide, 1007, 205, 82, 4, "#FFFFFF88", ctx.line("#00000000", 0), "roundRect", "decor-card-glint-2");
  shape(ctx, slide, 1532, 205, 58, 4, "#FFFFFF88", ctx.line("#00000000", 0), "roundRect", "decor-card-glint-3");
  for (const [x, y, s] of [
    [536, 165, 7],
    [575, 190, 4],
    [1015, 171, 5],
    [1081, 191, 7],
    [1536, 175, 5],
    [1584, 196, 4],
  ]) {
    shape(ctx, slide, x, y, s, s, "#E8B94588", ctx.line("#00000000", 0), "ellipse", "decor-dot");
  }
}

function addCertificate(ctx, slide) {
  shape(ctx, slide, 74, 142, 382, 262, C.paper, ctx.line(C.gold, 1.3), "rect", "certificate-paper");
  shape(ctx, slide, 84, 151, 362, 244, "#00000000", ctx.line("#74B84D", 1, "dashed"), "rect", "certificate-inner-dash");
  for (const [x, y, sx] of [[95, 160, 1], [407, 160, -1], [95, 366, 1], [407, 366, -1]]) {
    shape(ctx, slide, x, y, 30, 18, "#F8DDA155", ctx.line(C.gold, 1), "ellipse", "certificate-ornament");
    shape(ctx, slide, x + (sx > 0 ? 20 : -8), y + 8, 22, 10, "#F8DDA155", ctx.line(C.gold, 1), "ellipse", "certificate-ornament");
  }
  line(ctx, slide, 140, 162, 235, 0, "#8B5E1E", 1.2, "certificate-rule-top");
  line(ctx, slide, 140, 385, 235, 0, "#8B5E1E", 1.2, "certificate-rule-bottom");
  text(ctx, slide, "结 业 证 书", 174, 187, 170, 38, { fontSize: 28, bold: true, color: C.gold, align: "center" });
  text(ctx, slide, "李翰 同学：\n于2026.3.22至2026.4.11参加华清远见基于大模型+智能体的AI数据分析\n项目实战训练营，学习期满、答辩合格、准予结业。\n\n特颁发此证书，以资鼓励。", 113, 248, 276, 72, { fontSize: 8.4, color: "#222222", align: "left" });
  text(ctx, slide, "北京华清远见科技发展有限公司\n2026年4月12", 279, 338, 106, 24, { fontSize: 7.8, color: "#222222", align: "center" });
  shape(ctx, slide, 330, 318, 52, 52, "#00000000", ctx.line("#C94332", 2), "ellipse", "certificate-stamp");
  line(ctx, slide, 342, 344, 28, 0, "#C94332", 1.2, "certificate-stamp-line");
}

function addPhotoMock(ctx, slide) {
  shape(ctx, slide, 591, 151, 395, 259, "#DDE7E0", ctx.line("#74B84D", 1, "dashed"), "rect", "photo-frame");
  shape(ctx, slide, 600, 153, 86, 119, "#25292E", ctx.line("#1D2227", 1), "rect", "photo-cabinet-1");
  shape(ctx, slide, 689, 153, 76, 119, "#202429", ctx.line("#1D2227", 1), "rect", "photo-cabinet-2");
  shape(ctx, slide, 767, 151, 116, 64, "#87ADC8", ctx.line("#00000000", 0), "rect", "photo-curtain");
  shape(ctx, slide, 879, 153, 80, 95, "#ECECEC", ctx.line("#BBBBBB", 1), "rect", "photo-machine");
  shape(ctx, slide, 661, 277, 321, 81, "#0853A7", ctx.line("#06478F", 1), "rect", "photo-workbench");
  for (let i = 0; i < 10; i += 1) {
    line(ctx, slide, 671, 288 + i * 7, 287, 0, "#E7D64C", 1, "photo-drawer-line");
  }
  shape(ctx, slide, 594, 300, 120, 92, "#2C2F33", ctx.line("#1C1E21", 1), "rect", "photo-monitor");
  shape(ctx, slide, 723, 244, 64, 55, "#F2C1A7", ctx.line("#00000000", 0), "ellipse", "photo-head");
  shape(ctx, slide, 714, 226, 80, 42, "#2B211D", ctx.line("#00000000", 0), "ellipse", "photo-hair");
  shape(ctx, slide, 700, 299, 163, 94, "#F7F1E9", ctx.line("#CCCCCC", 1), "rect", "photo-shirt");
  for (let i = 0; i < 11; i += 1) {
    line(ctx, slide, 708 + i * 14, 309, 34, 56, "#111111", 1, "photo-shirt-pattern");
  }
  shape(ctx, slide, 777, 345, 116, 40, "#F1E3CC", ctx.line("#9D8B74", 1), "rect", "photo-notebook");
}

function addCard(ctx, slide, { x, title, type }) {
  shape(ctx, slide, x + 10, 160, 418, 598, "#00000022", ctx.line("#00000000", 0), "roundRect", "card-shadow");
  shape(ctx, slide, x, 150, 418, 608, C.white, ctx.line(C.green, 2), "roundRect", "card");
  shape(ctx, slide, x, 428, 418, 43, "linear(0deg, #065D36, #5F9430)", ctx.line("#00000000", 0), "rect", "card-title-band");
  text(ctx, slide, title, x, 432, 418, 36, { fontSize: 33, color: C.white, bold: true, align: "center", valign: "middle" });

  if (type === "certificate") {
    addCertificate(ctx, slide);
    text(ctx, slide, "李翰 同学:\n于2026.3.22至2026.4.11参加华\n清远见基于大模型+智能体的AI数\n据分析项目实战训练营，学习期\n满、答辩合格、准予结业。", x + 22, 500, 360, 215, {
      fontSize: 27,
      color: C.black,
      align: "left",
      insets: { left: 0, right: 0, top: 0, bottom: 0 },
    });
  } else if (type === "photo") {
    addPhotoMock(ctx, slide);
    text(ctx, slide, "实习内容描述（请填写）", x + 22, 494, 320, 40, { fontSize: 24, color: C.black });
  } else {
    shape(ctx, slide, x + 11, 154, 396, 257, "linear(90deg, #80EDB5, #EDF9EC)", ctx.line("#74B84D", 1, "dashed"), "rect", "photo-placeholder");
    text(ctx, slide, "实习照片", x + 88, 269, 242, 44, { fontSize: 25, color: "#4A7429", align: "center", valign: "middle" });
    text(ctx, slide, "实习内容描述（请填写）", x + 22, 494, 320, 40, { fontSize: 24, color: C.black });
  }
}

export async function slide01(presentation, ctx) {
  const slide = presentation.slides.add();

  shape(ctx, slide, 0, 0, 1600, 900, "linear(90deg, #F9FCFF, #EEF8EB)", ctx.line("#00000000", 0), "rect", "background");
  shape(ctx, slide, 0, 0, 1600, 900, "linear(90deg, #FFFFFF55, #D9F2DE44, #FFFFFF33)", ctx.line("#00000000", 0), "rect", "background-soft-layer");
  for (const [x, y, w, h, opacity] of [
    [760, 65, 325, 96, "44"],
    [1074, 59, 290, 86, "36"],
    [470, 76, 240, 70, "34"],
  ]) {
    shape(ctx, slide, x, y, w, h, `#FFFFFF${opacity}`, ctx.line("#00000000", 0), "ellipse", "background-cloud");
  }
  for (let i = 0; i < 21; i += 1) {
    const x = 10 + i * 78;
    line(ctx, slide, x, 652 - (i % 5) * 11, 24, -148 - (i % 4) * 16, "#7FAF6988", 1, "background-grass");
  }
  shape(ctx, slide, 0, 822, 1600, 78, "#67A83A", ctx.line("#00000000", 0), "rect", "bottom-green-wave");
  shape(ctx, slide, 682, 836, 918, 64, "#16975F", ctx.line("#00000000", 0), "rect", "bottom-deep-wave");
  line(ctx, slide, 0, 823, 1600, 32, "#FFFFFF", 3, "bottom-white-wave");

  text(ctx, slide, "实习经历", 73, 18, 240, 66, { fontSize: 48, color: C.green, bold: true, typeface: "Microsoft YaHei UI" });
  line(ctx, slide, 59, 88, 441, 0, "#006D43", 4, "title-underline");

  addDecorations(ctx, slide);
  addCard(ctx, slide, { x: 51, title: "实习单位一", type: "certificate" });
  addCard(ctx, slide, { x: 580, title: "实习单位二", type: "photo" });
  addCard(ctx, slide, { x: 1111, title: "实习单位三", type: "placeholder" });

  text(ctx, slide, "实践出真知，成长共进", 568, 785, 464, 54, {
    fontSize: 38,
    color: C.white,
    bold: true,
    typeface: "SimSun",
    align: "center",
    valign: "middle",
  });

  return slide;
}
