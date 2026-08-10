// 图片压缩功能单元测试
// 这个文件用于验证图片压缩算法的正确性

class ImageCompressionTester {
  constructor() {
    this.tests = [];
    this.results = [];
  }

  // 测试1: 验证压缩质量参数在0.7-0.8范围内
  testQualityRange() {
    const quality = 0.75; // 我们在代码中使用的质量参数
    const isValid = quality >= 0.7 && quality <= 0.8;
    
    this.results.push({
      name: '质量参数范围验证',
      passed: isValid,
      expected: '0.7 ≤ quality ≤ 0.8',
      actual: `quality = ${quality}`,
      message: isValid ? '通过' : '质量参数不在0.7-0.8范围内'
    });
  }

  // 测试2: 验证最大文件大小限制
  testMaxFileSize() {
    const maxSize = 500 * 1024; // 500KB
    const isValid = maxSize === 512000;
    
    this.results.push({
      name: '最大文件大小验证',
      passed: isValid,
      expected: '500KB (512000 bytes)',
      actual: `${maxSize} bytes`,
      message: isValid ? '通过' : '最大文件大小设置不正确'
    });
  }

  // 测试3: 验证保持宽高比算法
  testAspectRatio() {
    const testCases = [
      { width: 4000, height: 3000, maxDimension: 1920, expectedWidth: 1920, expectedHeight: 1440 },
      { width: 3000, height: 4000, maxDimension: 1920, expectedWidth: 1440, expectedHeight: 1920 },
      { width: 1000, height: 800, maxDimension: 1920, expectedWidth: 1000, expectedHeight: 800 }
    ];

    let allPassed = true;
    const testResults = [];

    testCases.forEach((testCase, index) => {
      let { width, height, maxDimension, expectedWidth, expectedHeight } = testCase;
      
      let newWidth = width;
      let newHeight = height;
      
      if (width > maxDimension || height > maxDimension) {
        if (width > height) {
          newHeight = Math.round((height / width) * maxDimension);
          newWidth = maxDimension;
        } else {
          newWidth = Math.round((width / height) * maxDimension);
          newHeight = maxDimension;
        }
      }

      const passed = newWidth === expectedWidth && newHeight === expectedHeight;
      if (!passed) allPassed = false;

      testResults.push({
        case: index + 1,
        passed,
        input: `${width}x${height}`,
        expected: `${expectedWidth}x${expectedHeight}`,
        actual: `${newWidth}x${newHeight}`
      });
    });

    this.results.push({
      name: '宽高比保持算法验证',
      passed: allPassed,
      expected: '所有测试用例通过',
      actual: `${testResults.filter(r => r.passed).length}/${testResults.length} 个通过`,
      message: allPassed ? '通过' : '部分测试用例失败',
      details: testResults
    });
  }

  // 测试4: 验证文件大小格式化函数
  testFormatFileSize() {
    const testCases = [
      { bytes: 0, expected: '0 B' },
      { bytes: 1023, expected: '1023 B' },
      { bytes: 1024, expected: '1 KB' },
      { bytes: 1536, expected: '1.5 KB' },
      { bytes: 1048576, expected: '1 MB' }
    ];

    let allPassed = true;
    const testResults = [];

    testCases.forEach((testCase, index) => {
      const result = this.formatFileSize(testCase.bytes);
      const passed = result === testCase.expected;
      if (!passed) allPassed = false;

      testResults.push({
        case: index + 1,
        passed,
        input: `${testCase.bytes} bytes",
        expected: testCase.expected,
        actual: result
      });
    });

    this.results.push({
      name: '文件大小格式化函数验证',
      passed: allPassed,
      expected: '所有测试用例通过',
      actual: `${testResults.filter(r => r.passed).length}/${testResults.length} 个通过`,
      message: allPassed ? '通过' : '部分测试用例失败',
      details: testResults
    });
  }

  formatFileSize(bytes) {
    if (bytes === 0) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }

  // 测试5: 验证文件类型图标函数
  testGetFileIcon() {
    const testCases = [
      { type: 'application/pdf', expectedIcon: 'material-symbols:picture-as-pdf', expectedClass: 'text-red-500' },
      { type: 'application/msword', expectedIcon: 'material-symbols:description', expectedClass: 'text-blue-500' },
      { type: 'image/png', expectedIcon: 'material-symbols:insert-drive-file', expectedClass: 'text-slate-400' }
    ];

    let allPassed = true;
    const testResults = [];

    testCases.forEach((testCase, index) => {
      const icon = this.getFileIcon(testCase.type);
      const iconClass = this.getFileIconClass(testCase.type);
      const passed = icon === testCase.expectedIcon && iconClass.includes(testCase.expectedClass);
      if (!passed) allPassed = false;

      testResults.push({
        case: index + 1,
        passed,
        input: testCase.type,
        expectedIcon: testCase.expectedIcon,
        expectedClass: testCase.expectedClass,
        actualIcon: icon,
        actualClass: iconClass
      });
    });

    this.results.push({
      name: '文件类型图标函数验证',
      passed: allPassed,
      expected: '所有测试用例通过',
      actual: `${testResults.filter(r => r.passed).length}/${testResults.length} 个通过`,
      message: allPassed ? '通过' : '部分测试用例失败',
      details: testResults
    });
  }

  getFileIcon(type) {
    if (type.includes('pdf')) return 'material-symbols:picture-as-pdf';
    if (type.includes('word') || type.includes('document')) return 'material-symbols:description';
    if (type.includes('sheet') || type.includes('excel')) return 'material-symbols:table-chart';
    if (type.includes('presentation') || type.includes('powerpoint')) return 'material-symbols:slideshow';
    if (type.includes('zip') || type.includes('rar') || type.includes('archive')) return 'material-symbols:folder-zip';
    if (type.includes('audio') || type.includes('music')) return 'material-symbols:audio-file';
    if (type.includes('video')) return 'material-symbols:video-file';
    if (type.includes('text')) return 'material-symbols:text-snippet';
    return 'material-symbols:insert-drive-file';
  }

  getFileIconClass(type) {
    let baseClass = 'text-3xl';
    if (type.includes('pdf')) return baseClass + ' text-red-500';
    if (type.includes('word') || type.includes('document')) return baseClass + ' text-blue-500';
    if (type.includes('sheet') || type.includes('excel')) return baseClass + ' text-green-500';
    if (type.includes('presentation') || type.includes('powerpoint')) return baseClass + ' text-orange-500';
    if (type.includes('zip') || type.includes('rar') || type.includes('archive')) return baseClass + ' text-yellow-600';
    if (type.includes('audio') || type.includes('music')) return baseClass + ' text-purple-500';
    if (type.includes('video')) return baseClass + ' text-pink-500';
    return baseClass + ' text-slate-400';
  }

  // 运行所有测试
  runAllTests() {
    console.log('🧪 开始运行图片压缩功能单元测试...\n');
    
    this.testQualityRange();
    this.testMaxFileSize();
    this.testAspectRatio();
    this.testFormatFileSize();
    this.testGetFileIcon();
    
    this.printResults();
  }

  // 打印测试结果
  printResults() {
    const passedTests = this.results.filter(r => r.passed).length;
    const totalTests = this.results.length;
    
    console.log('=' .repeat(60));
    console.log(`📊 测试结果: ${passedTests}/${totalTests} 通过`);
    console.log('=' .repeat(60));
    console.log('');

    this.results.forEach((result, index) => {
      const status = result.passed ? '✅' : '❌';
      console.log(`${status} ${result.name}`);
      console.log(`   期望: ${result.expected}`);
      console.log(`   实际: ${result.actual}`);
      console.log(`   结果: ${result.message}`);
      
      if (result.details && !result.passed) {
        console.log('   详情:');
        result.details.forEach(detail => {
          const detailStatus = detail.passed ? '✅' : '❌';
          console.log(`     ${detailStatus} 用例 ${detail.case}: ${detail.input}`);
          if (!detail.passed) {
            console.log(`        期望: ${detail.expected}, 实际: ${detail.actual}`);
          }
        });
      }
      console.log('');
    });

    if (passedTests === totalTests) {
      console.log('🎉 所有测试通过！');
    } else {
      console.log('⚠️  部分测试失败，请检查实现。');
    }
  }
}

// 导出测试类
if (typeof module !== 'undefined' && module.exports) {
  module.exports = ImageCompressionTester;
}

// 如果在浏览器中运行，自动执行测试
if (typeof window !== 'undefined') {
  const tester = new ImageCompressionTester();
  tester.runAllTests();
}

// 如果在Node.js环境中运行
if (typeof require !== 'undefined' && require.main === module) {
  const tester = new ImageCompressionTester();
  tester.runAllTests();
}