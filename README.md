# 密碼產生器 - 資料結構實作專案

## 📋 專案概述

這是一個結合GUI介面的密碼產生器，展示了多種資料結構的實作與效能分析。專案涵蓋了Hash Function的實作、資料結構效能比較，以及實際應用的整合。

## 🎯 專案目標

1. **資料結構設計與實作**: 實作多種Hash Function演算法
2. **效能評比與分析**: 比較不同資料結構和演算法的效能
3. **Java程式設計**: 建立完整的GUI應用程式

## 🚀 功能特色

### 主要功能
- **多種Hash Function**: 簡單除法、乘法Hash、通用Hash
- **自訂密碼設定**: 長度、字符類型、產生演算法
- **密碼強度評估**: 即時評估密碼安全性
- **歷史紀錄管理**: 使用ArrayList和Queue管理密碼歷史
- **效能分析工具**: 詳細分析各種演算法效能

### 資料結構應用
- **ArrayList**: 儲存完整密碼歷史
- **LinkedList (Queue)**: 管理最近產生的密碼
- **HashMap vs TreeMap**: 效能比較分析
- **Hash Function**: 三種不同實作策略

## 📁 檔案結構

```
password-generator/
├── PasswordGenerator.java      # 主程式 - GUI介面
├── HashFunction.java          # Hash Function抽象類別和實作
├── PerformanceAnalyzer.java   # 效能分析工具
├── README.md                  # 專案說明
└── compile_and_run.bat        # 編譯執行腳本
```

## 🔧 系統需求

- **Java版本**: JDK 8 或以上
- **作業系統**: Windows, macOS, Linux
- **記憶體**: 最少 512MB RAM
- **螢幕解析度**: 最少 800x600

## 📦 編譯與執行

### 方法一：使用批次檔 (Windows)
```bash
double-click compile_and_run.bat
```

### 方法二：手動編譯
```bash
# 編譯所有Java檔案
javac *.java

# 執行主程式
java PasswordGenerator

# 或直接執行效能分析器
java PerformanceAnalyzer
```

### 方法三：使用IDE
1. 將所有`.java`檔案匯入IDE (如Eclipse, IntelliJ IDEA)
2. 確保所有檔案在同一個package中
3. 執行`PasswordGenerator.main()`方法

## 💡 使用說明

### 基本操作
1. **設定密碼參數**: 調整長度、選擇字符類型
2. **選擇演算法**: 三種Hash Function可選
3. **產生密碼**: 點擊"產生密碼"按鈕
4. **檢視強度**: 即時顯示密碼安全評分
5. **管理歷史**: 查看最近產生的密碼

### 效能分析
1. 點擊"效能分析"按鈕開啟分析工具
2. 選擇測試類型和次數
3. 點擊"開始分析"查看詳細報告
4. 比較不同演算法的效能差異

## 🔍 技術重點

### Hash Function 實作
1. **簡單除法Hash**: `h(k) = k mod m`
2. **乘法Hash**: `h(k) = floor(m * (k * A mod 1))`
3. **通用Hash**: `h(k) = ((a*k + b) mod p) mod m`

### 資料結構選擇
- **ArrayList**: O(1)隨機存取，適合歷史紀錄儲存
- **LinkedList Queue**: O(1)插入刪除，適合FIFO操作
- **HashMap**: O(1)平均查找時間
- **TreeMap**: O(log n)查找，但保持排序

### 效能測試項目
- Hash Function分布均勻性
- 密碼產生速度比較
- 不同資料結構操作效能
- 記憶體使用分析

## 📊 分析結果範例

### Hash Function效能 (10000次測試)
```
密碼長度: 16 字符
----------------------------------------
簡單除法Hash       : 總時間   45.23 ms, 平均   0.0045 ms
乘法Hash           : 總時間   52.67 ms, 平均   0.0053 ms  
通用Hash           : 總時間   48.91 ms, 平均   0.0049 ms
```

### 分布均勻性分析
```
通用Hash (Universal Hashing)
----------------------------------------
期望值: 100.00
最小值: 87, 最大值: 114
標準差: 6.73
均勻性: 93.27%
```

## 🎓 學習重點

### 資料結構概念
1. **Hash Table實作**: 理解不同hash策略的特性
2. **時間複雜度**: 比較各種操作的效能
3. **空間複雜度**: 分析記憶體使用效率
4. **資料結構選擇**: 根據需求選擇適當結構

### 程式設計技巧
1. **GUI設計**: Swing元件的使用
2. **事件處理**: ActionListener的實作
3. **多執行緒**: SwingWorker避免UI凍結
4. **物件導向**: 抽象類別和繼承的應用

## 🔒 安全性考量

- 使用`SecureRandom`提供加密安全的隨機數
- 實作多種Hash演算法避免單點依賴
- 密碼強度即時評估提醒使用者
- 不在記憶體中長期保存敏感資料

## 🛠 可擴展功能

### 進階功能建議
1. **儲存到檔案**: 將密碼匯出為加密檔案
2. **自訂字符集**: 允許使用者定義字符範圍
3. **密碼模板**: 預設常用密碼格式
4. **批次產生**: 一次產生多個密碼
5. **網路同步**: 雲端備份功能

### 演算法擴展
1. **更多Hash Function**: SHA-256, bcrypt等
2. **機器學習**: 基於使用模式優化
3. **量子安全**: 後量子密碼學演算法
4. **生物特徵**: 結合生物識別的密碼

## 📚 參考資料

- **Cormen, T.H.** et al. (2009). *Introduction to Algorithms, 3rd Edition*
- **Sedgewick, R.** (2011). *Algorithms, 4th Edition*
- **Oracle Java Documentation**: Swing GUI Components
- **NIST Special Publication 800-63B**: Authentication and Lifecycle Management

## 👥 專案資訊

- **開發語言**: Java
- **GUI框架**: Swing
- **設計模式**: MVC, Observer, Strategy
- **測試方法**: 效能基準測試

## 📄 授權資訊

本專案僅供學習和教育目的使用。請遵循相關的開源授權條款。

---

*這個專案展示了如何將理論的資料結構知識應用到實際的軟體開發中，是學習演算法和程式設計的優秀範例。*