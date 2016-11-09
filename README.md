# 特性

- 绘制与控件分离，Chart类似于Drawable，可在任意控件上绘制
- 设计简单，可随意修改

# 使用

目前提供的图表类型有：
- LineChart
- BarChart
- PieChart

提供了两种View可供直接使用，每个View可绘制任意多个Chart
- ChartView：基于View
- ChartTextureView， 基于TextureView，性能稍高，仅限于API 14以上使用