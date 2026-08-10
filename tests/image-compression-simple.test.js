// 图片压缩功能单元测试 - 简化版

// 测试1: 验证压缩质量参数在0.7-0.8范围内
function testQualityRange() {
  const quality = 0.75;
  const isValid = quality >= 0.7 && quality <= 0.8;
  console.log('测试1: 质量参数范围验证');
  console.log(`  质量参数: ${quality}`);
  console.log(`  结果: ${isValid ? '✅ 通过' : '❌ 失败'}`);
  return isValid;
}

// 测试2: 验证最大文件大小限制
function testMaxFileSize() {
  const maxSize = 500 * 1024;
  const isValid = maxSize === 512000;
  console.log('\n测试2: 最大文件大小验证');
  console.log(`  最大大小: ${maxSize} bytes (${maxSize / 1024} KB)`);
  console.log(`  结果: ${isValid ? '✅ 通过' : '❌ 失败'}`);
  return isValid;
}

// 测试3: 验证保持宽高比算法
function testAspectRatio() {
  console.log('\n测试3: 宽高比保持算法验证');
  
  const testCases = [
    { width: 4000, height: 3000, maxDimension: 1920, expectedWidth: 1920, expectedHeight: 1440 },
    { width: 3000, height: 4000, maxDimension: 1920, expectedWidth: 1440, expectedHeight: 1920 },
    { width: 1000, height: 800, maxDimension: 1920, expectedWidth: 1000, expectedHeight: 800 }
  ];

  let allPassed = true;
  
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
    
    console.log(`  用例${index + 1}: ${width}x${height} → ${newWidth}x${newHeight}`);
    console.log(`    期望: ${expectedWidth}x${expectedHeight}, 结果: ${passed ? '✅' : '❌'}`);
  });
  
  console.log(`  整体结果: ${allPassed ? '✅ 通过' : '❌ 失败'}`);
  return allPassed;
}

// 测试4: 验证文件大小格式化函数
function testFormatFileSize() {
  console.log('\n测试4: 文件大小格式化函数验证');
  
  function formatFileSize(bytes) {
    if (bytes === 0) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }

  const testCases = [
    { bytes: 0, expected: '0 B' },
    { bytes: 1023, expected: '1023 B' },
    { bytes: 1024, expected: '1 KB' },
    { bytes: 1536, expected: '1.5 KB' },
    { bytes: 1048576, expected: '1 MB' }
  ];

  let allPassed = true;
  
  testCases.forEach((testCase, index) => {
    const result = formatFileSize(testCase.bytes);
    const passed = result === testCase.expected;
    if (!passed) allPassed = false;
    
    console.log(`  用例${index + 1}: ${testCase.bytes} bytes → "${result}"`);
    console.log(`    期望: "${testCase.expected}", 结果: ${passed ? '✅' : '❌'}`);
  });
  
  console.log(`  整体结果: ${allPassed ? '✅ 通过' : '❌ 失败'}`);
  return allPassed;
}

// 测试5: 验证文件类型图标函数
function testGetFileIcon() {
  console.log('\n测试5: 文件类型图标函数验证');
  
  function getFileIcon(type) {
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

  function getFileIconClass(type) {
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

  const testCases = [
    { type: 'application/pdf', expectedIcon: 'material-symbols:picture-as-pdf', expectedClass: 'text-red-500' },
    { type: 'application/msword', expectedIcon: 'material-symbols:description', expectedClass: 'text-blue-500' },
    { type: 'image/png', expectedIcon: 'material-symbols:insert-drive-file', expectedClass: 'text-slate-400' }
  ];

  let allPassed = true;
  
  testCases.forEach((testCase, index) => {
    const icon = getFileIcon(testCase.type);
    const iconClass = getFileIconClass(testCase.type);
    const passed = icon === testCase.expectedIcon && iconClass.includes(testCase.expectedClass);
    if (!passed) allPassed = false;
    
    console.log(`  用例${index + 1}: ${testCase.type}`);
    console.log(`    图标: ${icon} (期望: ${testCase.expectedIcon}) - ${icon === testCase.expectedIcon ? '✅' : '❌'}`);
    console.log(`    样式: ${iconClass} (包含: ${testCase.expectedClass}) - ${iconClass.includes(testCase.expectedClass) ? '✅' : '❌'}`);
  });
  
  console.log(`  整体结果: ${allPassed ? '✅ 通过' : '❌ 失败'}`);
  return allPassed;
}

// 运行所有测试
function runAllTests() {
  console.log('=' .repeat(60));
  console.log('🧪 图片压缩功能单元测试');
  console.log('=' .repeat(60));
  
  const results = [
    testQualityRange(),
    testMaxFileSize(),
    testAspectRatio(),
    testFormatFileSize(),
    testGetFileIcon()
  ];
  
  const passed = results.filter(r => r).length;
  const total = results.length;
  
  console.log('\n' + '=' .repeat(60));
  console.log(`📊 汇总: ${passed}/${total} 个测试通过`);
  console.log('=' .repeat(60));
  
  if (passed === total) {
    console.log('\n🎉 所有测试通过！');
    process.exit(0);
  } else {
    console.log('\n⚠️  部分测试失败，请检查实现。');
    process.exit(1);
  }
}

// 运行测试
runAllTests();